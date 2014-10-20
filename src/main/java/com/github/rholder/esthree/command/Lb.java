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
import com.amazonaws.services.s3.model.Bucket;
import com.github.rholder.esthree.util.RetryUtils;
import com.github.rholder.retry.RetryException;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Lb implements Callable<Integer> {

    public AmazonS3Client amazonS3Client;

    public String listBucketFormat;
    public PrintStream printStream;

    public Lb(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public Lb withListBucketFormat(String listBucketFormat) {
        this.listBucketFormat = listBucketFormat;
        return this;
    }

    public Lb withPrintStream(PrintStream printStream) {
        this.printStream = printStream;
        return this;
    }

    @Override
    public Integer call() throws Exception {
        for (Bucket b : listBuckets()) {
            printStream.println(String.format(listBucketFormat,
                    b.getCreationDate(),
                    b.getName(),
                    b.getOwner()));
        }
        return 0;
    }

    /**
     * Return all of the buckets as a list.
     */
    @SuppressWarnings("unchecked")
    public List<Bucket> listBuckets() throws ExecutionException, RetryException {
        return (List<Bucket>) RetryUtils.AWS_RETRYER.call(new Callable<Object>() {
            public List<Bucket> call() throws Exception {
                return amazonS3Client.listBuckets();
            }
        });
    }
}
