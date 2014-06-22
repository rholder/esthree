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

package com.github.rholder.esthree.cli;

import com.github.rholder.esthree.command.Get;
import com.github.rholder.esthree.progress.MutableProgressListener;
import com.github.rholder.esthree.progress.PrintingProgressListener;
import com.github.rholder.esthree.util.S3PathUtils;
import io.airlift.command.Arguments;
import io.airlift.command.Command;
import io.airlift.command.Option;

import java.io.File;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.getPrefixLength;

@Command(name = "get", description = "Download a file from S3 with the target bucket and key")
public class GetCommand extends EsthreeCommand {

    @Option(name = {"-np", "--no-progress"}, description = "Don't print a progress bar")
    public Boolean progress;

    @Arguments(description = "[target bucket and key]", usage = "The target bucket and key, as in \"s3://bucket/foo.html\"", required = true)
    public List<String> parameters;

    @Override
    public void run() {
        String target = parameters.get(0);
        String bucket = S3PathUtils.getBucket(target);
        String key = S3PathUtils.getPrefix(target);
        progress = progress == null;

        // TODO validate params here
        File outputFile;
        if(parameters.size() > 1) {
            outputFile = new File(parameters.get(1));
        } else {
            // infer filename from file being fetched if unspecified
            String path = S3PathUtils.getPrefix(target);
            int index = path.lastIndexOf(File.separatorChar);
            int prefixLength = getPrefixLength(path);

            String fileName;
            if (index < prefixLength) {
                fileName =  path.substring(prefixLength);
            } else {
                fileName = path.substring(index + 1);
            }
            outputFile = new File(fileName);
        }

        try {
            MutableProgressListener progressListener = null;
            if(progress) {
                progressListener = new PrintingProgressListener(output);
            }

            new Get(amazonS3Client, bucket, key, outputFile)
                    .withProgressListener(progressListener)
                    .call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
