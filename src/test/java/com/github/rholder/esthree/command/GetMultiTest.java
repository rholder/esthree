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

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.github.rholder.esthree.progress.PrintingProgressListener;
import com.github.rholder.esthree.util.FileChunker;
import com.google.common.primitives.Ints;
import org.apache.commons.io.IOUtils;
import org.apache.ivy.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.rholder.esthree.util.FileChunker.FilePart;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetMultiTest {

    // MD5 of 100 'a' characters
    public static final String A_MD5 = "36a92cc94a9e0fa21f625f8bfb007adf";

    public static final long CONTENT_LENGTH = 100;
    public static final int CHUNK_SIZE = 23;

    @Test
    public void happyGetMulti() throws Exception {
        AmazonS3Client client = createMockedClient();

        File tmpFile = File.createTempFile("testFile", ".test");
        tmpFile.deleteOnExit();

        GetMultipart gm = new GetMultipart(client, "testBucket", "testKey", tmpFile);
        gm.withChunkSize(CHUNK_SIZE);
        gm.call();
    }

    @Test
    public void happyGetMultiWithProgress() throws Exception {
        AmazonS3Client client = createMockedClient();

        File tmpFile = File.createTempFile("testFile", ".test");
        tmpFile.deleteOnExit();

        GetMultipart gm = new GetMultipart(client, "testBucket", "testKey", tmpFile);
        gm.withChunkSize(CHUNK_SIZE);
        gm.withProgressListener(new PrintingProgressListener(System.out));
        gm.call();
    }

    public AmazonS3Client createMockedClient() throws IOException {

        // mock the ObjectMetadata for a 100 bytes of a's
        ObjectMetadata om = mock(ObjectMetadata.class);
        when(om.getContentLength()).thenReturn(CONTENT_LENGTH);
        when(om.getETag()).thenReturn(A_MD5);

        List<S3Object> os = new ArrayList<S3Object>();
        List<FilePart> parts = FileChunker.chunk(CONTENT_LENGTH, CHUNK_SIZE);
        for(final FilePart p : parts) {
            // fake some content InputStream's by filling in chunks of a's
            S3Object o = mock(S3Object.class);
            S3ObjectInputStream input = new S3ObjectInputStream(
                    IOUtils.toInputStream(StringUtils.repeat("a", Ints.checkedCast(p.end - p.start + 1))),
                    null);
            // mock the returned content
            when(o.getObjectContent()).thenReturn(input);
            os.add(o);
        }
        Assert.assertEquals(5, os.size());

        // mock the AmazonS3Client
        AmazonS3Client client = mock(AmazonS3Client.class);
        when(client.getObjectMetadata(anyString(), anyString())).thenReturn(om);
        when(client.getObject(any(GetObjectRequest.class))).thenReturn(os.get(0), os.get(1), os.get(2), os.get(3), os.get(4));

        return client;
    }
}
