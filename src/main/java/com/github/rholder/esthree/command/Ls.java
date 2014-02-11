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

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.github.rholder.retry.RetryException;
import com.github.rholder.esthree.util.RetryUtils;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Ls implements Callable<Integer> {

    // that's pretty close to s3cmd
    public static final String DEFAULT_LIST_FORMAT = "%1$tF %1$tR %2$9s   s3://%3$s/%4$s";

    public static final Integer AWS_MAX_KEYS = 1000;

    public static final BigInteger AWS_MAX_KEYS_BIG = new BigInteger(AWS_MAX_KEYS.toString());
    public static final BigInteger ONE = new BigInteger("1");

    public String bucket;
    public String prefix;
    public BigInteger limit;

    public String listFormat = DEFAULT_LIST_FORMAT;
    public PrintStream printStream;

    public Ls(String bucket) {
        this.bucket = bucket;
    }

    public Ls withPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public Ls withLimit(BigInteger limit) {
        this.limit = limit;
        return this;
    }

    public Ls withListFormat(String listFormat) {
        this.listFormat = listFormat;
        return this;
    }

    public Ls withPrintStream(PrintStream printStream) {
        this.printStream = printStream;
        return this;
    }

    @Override
    public Integer call() throws Exception {

        BigInteger count = new BigInteger("0");
        String nextMarker = null;
        int nextLimit;
        do {
            // no limit always requests the max
            nextLimit = AWS_MAX_KEYS;
            if(limit != null) {
                // if it is set, limit the next request to no more than the max that are still available
                nextLimit = limit.subtract(count).min(AWS_MAX_KEYS_BIG).intValue();
            }

            if(nextLimit > 0) {
                ObjectListing o = list(bucket, prefix, nextMarker, nextLimit);
                for (S3ObjectSummary os : o.getObjectSummaries()) {
                    if (limit == null || count.compareTo(limit) <= 0) {
                        printStream.println(String.format(listFormat,
                                os.getLastModified(),
                                os.getSize(),
                                os.getBucketName(),
                                os.getKey(),
                                os.getETag(),
                                os.getOwner(),
                                os.getStorageClass()));
                        nextMarker = o.getNextMarker();
                        count = count.add(ONE);
                    } else {
                        // bail out when we've printed all we can
                        nextMarker = null;
                    }
                }
            }
        } while (nextMarker != null && nextLimit > 0);

        return 0;
    }

    /**
     * Return the ObjectListing starting at the given marker. The number of
     * ObjectSummaries is bounded to be 1000 (this is the Amazon S3 default,
     * which we're also setting here explicitly to minimize future SDK update
     * implications).
     *
     * @param bucket the bucket to list
     * @param prefix the prefix (can be null if none specified)
     * @param marker the last marker from a previous call or null
     * @param limit  return no more than this many
     */
    public static ObjectListing list(final String bucket, final String prefix, final String marker, final int limit) throws ExecutionException, RetryException {
        return (ObjectListing) RetryUtils.AWS_RETRYER.call(new Callable<Object>() {
            public ObjectListing call() throws Exception {

                ListObjectsRequest lor = new ListObjectsRequest()
                        .withBucketName(bucket)
                        .withMarker(marker)
                        .withMaxKeys(limit);
                if (prefix != null) {
                    lor.withPrefix(prefix);
                }
                return new AmazonS3Client(new DefaultAWSCredentialsProviderChain()).listObjects(lor);
            }
        });
    }
}
