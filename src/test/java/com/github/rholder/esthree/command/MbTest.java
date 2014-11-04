package com.github.rholder.esthree.command;

import com.amazonaws.services.s3.AmazonS3Client;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MbTest {

    @Test
    public void noParameters() throws Exception {
        AmazonS3Client client = mock(AmazonS3Client.class);
        Mb mb = new Mb(client, null, null);
        mb.call();

        verify(client, times(1)).createBucket(isNull(String.class), isNull(String.class));
    }

    @Test
    public void happyPathBucket() throws Exception {
        AmazonS3Client client = mock(AmazonS3Client.class);
        Mb mb = new Mb(client, "meep", null);

        Assert.assertEquals(mb.bucket, "meep");
        Assert.assertEquals(mb.region, null);
        mb.call();

        verify(client, times(1)).createBucket(matches("meep"), isNull(String.class));
    }

    @Test
    public void happyPathBucketRegion() throws Exception {
        AmazonS3Client client = mock(AmazonS3Client.class);
        Mb mb = new Mb(client, "beep", "boop");

        Assert.assertEquals(mb.bucket, "beep");
        Assert.assertEquals(mb.region, "boop");
        mb.call();

        verify(client, times(1)).createBucket(matches("beep"), matches("boop"));
    }
}
