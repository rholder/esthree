package com.github.rholder.esthree.cli;


import com.amazonaws.services.s3.AmazonS3Client;
import com.github.rholder.esthree.AmazonS3ClientMockUtils;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class LsCommandTest {

    @Test
    public void happyPath() throws IOException {
        AmazonS3Client client = AmazonS3ClientMockUtils.createMockedClient(100, 1);

        LsCommand lsCommand = new LsCommand();
        lsCommand.parameters = Lists.newArrayList("s3://foo/");
        lsCommand.amazonS3Client = client;

        lsCommand.parse();
    }

    @Test
    public void stillHappyPath() throws IOException {
        AmazonS3Client client = AmazonS3ClientMockUtils.createMockedClient(100, 1);

        LsCommand lsCommand = new LsCommand();
        lsCommand.parameters = Lists.newArrayList("s3://foo/");
        lsCommand.amazonS3Client = client;

        lsCommand.parse();
    }

    @Test
    public void happyPathWithPrefix() throws IOException {
        AmazonS3Client client = AmazonS3ClientMockUtils.createMockedClient(100, 1);

        LsCommand lsCommand = new LsCommand();
        lsCommand.parameters = Lists.newArrayList("s3://foo/bar");
        lsCommand.amazonS3Client = client;

        lsCommand.parse();
    }

    @Test
    public void garbagePath() throws IOException {
        LsCommand lsCommand = new LsCommand();
        lsCommand.parameters = Lists.newArrayList("potato");

        try {
            lsCommand.parse();
            Assert.fail("Expected an IllegalArgumentException for garbage path");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Could not determine target bucket"));
        }
    }
}
