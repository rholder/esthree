/*
 * Copyright 2014 Ray Holder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rholder.esthree.command;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.internal.TransferProgressImpl;
import com.amazonaws.util.BinaryUtils;
import com.github.rholder.esthree.util.PrintingProgressListener;
import com.github.rholder.esthree.util.RetryUtils;
import com.github.rholder.retry.RetryException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.github.rholder.esthree.util.FileChunker.FilePart;
import static com.github.rholder.esthree.util.FileChunker.chunk;

public class GetMultipart implements Callable<Integer> {

    // TODO parameterize internal buffer size?
    public static final int DEFAULT_BUF_SIZE = 4096 * 4;
    public static final int DEFAULT_CHUNK_SIZE = 1024 * 1024 * 5; // 5 MB

    public AmazonS3Client amazonS3Client;
    public String bucket;
    public String key;
    public RandomAccessFile output;

    private Integer chunkSize;
    private PrintingProgressListener progressListener;

    private MessageDigest currentDigest;
    private List<FilePart> fileParts;
    private long contentLength;

    public GetMultipart(AmazonS3Client amazonS3Client, String bucket, String key, File outputFile) throws FileNotFoundException {
        this.amazonS3Client = amazonS3Client;
        this.bucket = bucket;
        this.key = key;
        this.output = new RandomAccessFile(outputFile, "rw");
    }

    public GetMultipart withProgressListener(PrintingProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    public GetMultipart withChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
        return this;
    }

    @Override
    public Integer call() throws Exception {
        ObjectMetadata om = amazonS3Client.getObjectMetadata(bucket, key);
        contentLength = om.getContentLength();

        // this is the most up to date digest, it's initialized here but later holds the most up to date valid digest
        currentDigest = MessageDigest.getInstance("MD5");
        chunkSize = chunkSize == null ? DEFAULT_CHUNK_SIZE : chunkSize;
        fileParts = chunk(contentLength, chunkSize);
        for (FilePart fp : fileParts) {

            /*
             * We'll need to compute the digest on the full incoming stream for
             * each valid chunk that comes in. Invalid chunks will need to be
             * recomputed and fed through a copy of the MD5 that was valid up
             * until the latest chunk.
             */
            currentDigest = retryingGetWithRange(fp.start, fp.end);
        }

        String fullETag = om.getETag();
        if (!fullETag.contains("-")) {
            byte[] expected = BinaryUtils.fromHex(fullETag);
            byte[] current = currentDigest.digest();
            if (!Arrays.equals(expected, current)) {
                throw new AmazonClientException("Unable to verify integrity of data download.  "
                        + "Client calculated content hash didn't match hash calculated by Amazon S3.  "
                        + "The data may be corrupt.");
            }
        } else {
            // TODO log warning that we can't validate the MD5
            System.err.println("MD5 does not exist on AWS for file, calculated value: " + BinaryUtils.toHex(currentDigest.digest()));
        }
        // TODO add ability to resume from previously downloaded chunks
        // TODO add rate limiter

        return 0;
    }

    public MessageDigest retryingGetWithRange(final long start, final long end)
            throws ExecutionException, RetryException {

        return (MessageDigest) RetryUtils.AWS_RETRYER.call(new Callable<Object>() {
            public MessageDigest call() throws Exception {

                long totalBytes = end - start + 1;
                TransferProgressImpl transferProgress = new TransferProgressImpl();
                transferProgress.setTotalBytesToTransfer(totalBytes);

                if (progressListener != null) {
                    progressListener.withTransferProgress(transferProgress)
                            .withCompleted(start / (100.0 * contentLength))
                            .withMultiplier((1.0 * totalBytes / chunkSize) / fileParts.size());
                }

                GetObjectRequest req = new GetObjectRequest(bucket, key)
                        .withRange(start, end);

                S3Object s3Object = amazonS3Client.getObject(req);
                InputStream input = null;
                try {
                    // seek to the start of the chunk in the file, just in case we're retrying
                    output.seek(start);
                    input = s3Object.getObjectContent();

                    return copyAndHash(input, totalBytes, transferProgress);
                } finally {
                    IOUtils.closeQuietly(input);
                }
            }
        });
    }

    public MessageDigest copyAndHash(InputStream input, long totalBytes, TransferProgressImpl transferProgress)
            throws IOException, CloneNotSupportedException {

        // clone the current digest, such that it remains unchanged in this method
        MessageDigest computedDigest = (MessageDigest) currentDigest.clone();
        byte[] buffer = new byte[DEFAULT_BUF_SIZE];

        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            if (progressListener != null) {
                transferProgress.updateProgress(n);
                progressListener.progressChanged(new ProgressEvent(n));
            }
            computedDigest.update(buffer, 0, n);
            count += n;
        }

        // verify that at least this many bytes were read
        if (totalBytes != count) {
            throw new IOException(String.format("%d bytes downloaded instead of expected %d bytes", count, totalBytes));
        }
        return computedDigest;
    }
}
