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
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.github.rholder.esthree.util.PrintingProgressListener;

import java.io.File;
import java.util.concurrent.Callable;

public class Get implements Callable<Integer> {

    public String bucket;
    public String key;
    public File outputFile;

    public PrintingProgressListener progressListener;

    public Get(String bucket, String key, File outputFile) {
        this.bucket = bucket;
        this.key = key;
        this.outputFile = outputFile;
    }

    public Get withProgressListener(PrintingProgressListener progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    @Override
    public Integer call() throws Exception {
        TransferManager t = new TransferManager(new DefaultAWSCredentialsProviderChain().getCredentials());
        Download d = t.download(bucket, key, outputFile);
        if (progressListener != null) {
            progressListener.withTransferProgress(d.getProgress());
            d.addProgressListener(progressListener);
        }
        d.waitForCompletion();

        String eTag = d.getObjectMetadata().getETag();
        String contentMD5 = d.getObjectMetadata().getContentMD5();
        if(eTag.equals(contentMD5)) {
            return 0;
        } else {
            System.err.println("MD5's do not match :(");
            return -1;
        }
    }
}
