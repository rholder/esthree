package com.github.rholder.esthree.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.github.rholder.retry.RetryException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class RetryUtilsTest extends RetryUtils {

    public int tryCount = 0;

    @Test
    public void retry404() throws ExecutionException, RetryException {

        AWS_RETRYER.call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if(tryCount < 2) {
                    tryCount++;
                    throw newExceptionWithCode(404);
                } else {
                    return null;
                }
            }
        });

        Assert.assertEquals(2, tryCount);
    }

    @Test
    public void retry501() throws ExecutionException, RetryException {

        AWS_RETRYER.call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if(tryCount < 2) {
                    tryCount++;
                    throw newExceptionWithCode(501);
                } else {
                    return null;
                }
            }
        });

        Assert.assertEquals(2, tryCount);
    }

    @Test
    public void retryIOException() throws ExecutionException, RetryException {

        AWS_RETRYER.call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if(tryCount < 2) {
                    tryCount++;
                    throw new IOException("disk on fire");
                } else {
                    return null;
                }
            }
        });

        Assert.assertEquals(2, tryCount);
    }

    @Test
    public void retryUnhandled() throws ExecutionException, RetryException {

        try {
            AWS_RETRYER.call(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    if (tryCount < 2) {
                        tryCount++;
                        throw new RuntimeException("wormhole opened up");
                    } else {
                        return null;
                    }
                }
            });
            Assert.fail("Expected RuntimeException");
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getMessage().contains("wormhole"));
        }

        Assert.assertEquals(1, tryCount);
    }

    @Test
    public void isEqual() {
        Assert.assertFalse(isEqual(new Object(), null));
        Assert.assertFalse(isEqual(null, new Object()));
    }

    public AmazonClientException newExceptionWithCode(int statusCode) {
        AmazonServiceException cause = new AmazonServiceException("The bad thing");
        cause.setStatusCode(statusCode);

        return new AmazonClientException("Something bad happened", cause);
    }
}
