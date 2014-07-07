package com.github.rholder.esthree.command;

import com.amazonaws.services.s3.AmazonS3Client;
import com.github.rholder.esthree.progress.PrintingProgressListener;
import org.junit.Test;

import java.io.File;

import static com.github.rholder.esthree.AmazonS3ClientMockUtils.createMockedClient;

public class GetTest {
    public static final int CHUNK_SIZE = 100;
    public static final int EXPECTED_CHUNKS = 1;

    @Test
    public void happyGetMulti() throws Exception {
        AmazonS3Client client = createMockedClient(CHUNK_SIZE, EXPECTED_CHUNKS);

        File tmpFile = File.createTempFile("testFile", ".test");
        tmpFile.deleteOnExit();

        Get get = new Get(client, "testBucket", "testKey", tmpFile);
        get.call();
    }

    @Test
    public void happyGetMultiWithProgress() throws Exception {
        AmazonS3Client client = createMockedClient(CHUNK_SIZE, EXPECTED_CHUNKS);

        File tmpFile = File.createTempFile("testFile", ".test");
        tmpFile.deleteOnExit();

        Get get = new Get(client, "testBucket", "testKey", tmpFile);
        get.withProgressListener(new PrintingProgressListener(System.out));
        get.call();
    }

}
