package com.github.rholder.esthree.command;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PutTest {

    @Test
    public void noParameters() throws Exception {
        AmazonS3Client client = mock(AmazonS3Client.class);
        when(client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        Put put = new Put(client, null, null, null, null);
        put.call();

        verify(client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void happyPath() throws Exception {
        AmazonS3Client client = mock(AmazonS3Client.class);
        when(client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        Put put = new Put(client, "beep", "boop", new File("testfile"), Maps.<String, String>newHashMap());

        Assert.assertEquals(put.bucket, "beep");
        Assert.assertEquals(put.key, "boop");
        Assert.assertEquals(put.inputFile, new File("testfile"));
        put.call();

        verify(client, times(1)).putObject(any(PutObjectRequest.class));
    }
}
