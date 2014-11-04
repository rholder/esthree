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
import com.github.rholder.esthree.progress.PrintingProgressListener;
import com.github.rholder.esthree.progress.TimeProvider;
import org.junit.Test;

import java.io.File;

import static com.github.rholder.esthree.AmazonS3ClientMockUtils.createMockedClient;

public class GetMultiTest {

    public static final int CHUNK_SIZE = 23;
    public static final int EXPECTED_CHUNKS = 5;

    @Test
    public void happyGetMulti() throws Exception {
        AmazonS3Client client = createMockedClient(CHUNK_SIZE, EXPECTED_CHUNKS);

        File tmpFile = File.createTempFile("testFile", ".test");
        tmpFile.deleteOnExit();

        GetMultipart gm = new GetMultipart(client, "testBucket", "testKey", tmpFile, true);
        gm.withChunkSize(CHUNK_SIZE);
        gm.call();
    }

    @Test
    public void happyGetMultiWithProgress() throws Exception {
        AmazonS3Client client = createMockedClient(CHUNK_SIZE, EXPECTED_CHUNKS);

        File tmpFile = File.createTempFile("testFile", ".test");
        tmpFile.deleteOnExit();

        GetMultipart gm = new GetMultipart(client, "testBucket", "testKey", tmpFile, true);
        gm.withChunkSize(CHUNK_SIZE);
        gm.withProgressListener(new PrintingProgressListener(System.out, new TimeProvider()));
        gm.call();
    }
}
