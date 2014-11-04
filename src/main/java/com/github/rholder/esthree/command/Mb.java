package com.github.rholder.esthree.command;

import com.amazonaws.services.s3.AmazonS3Client;

import java.util.concurrent.Callable;

public class Mb implements Callable<Integer> {
    public AmazonS3Client amazonS3Client;
    public String bucket;
    public String region;

    public Mb(AmazonS3Client amazonS3Client, String bucket, String region) {
        this.amazonS3Client = amazonS3Client;
        this.bucket = bucket;
        this.region = region;
    }

    @Override
    public Integer call() throws Exception {
        amazonS3Client.createBucket(bucket, region);
        return 0;
    }
}
