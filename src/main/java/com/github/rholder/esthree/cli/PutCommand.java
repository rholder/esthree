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

import com.github.rholder.esthree.command.Put;
import com.github.rholder.esthree.progress.MutableProgressListener;
import com.github.rholder.esthree.progress.PrintingProgressListener;
import com.github.rholder.esthree.util.S3PathUtils;
import io.airlift.command.Arguments;
import io.airlift.command.Command;
import io.airlift.command.Option;

import java.io.File;
import java.util.List;

import static com.google.common.base.Objects.firstNonNull;
import static java.util.Collections.emptyList;

@Command(name = "put", description = "List the target bucket with an optional prefix")
public class PutCommand extends EsthreeCommand {

    @Option(name = {"-np", "--no-progress"}, description = "Don't print a progress bar")
    public Boolean progress;

    @Arguments(description = "Upload a file to S3 with the target bucket and optionally the key, as in \"foo.txt s3://bucket/foo.html\"",
            usage = "[filename] [target bucket and key]")
    public List<String> parameters;

    public String bucket;
    public String key;
    public File outputFile;
    public MutableProgressListener progressListener;

    @Override
    public void parse() {
        if (firstNonNull(parameters, emptyList()).size() == 0) {
            showUsage(commandMetadata);
            throw new IllegalArgumentException("No arguments specified");
        }

        // TODO foo s3://bucket  <--- support this?
        if (parameters.size() != 2) {
            output.print("Invalid number of arguments");
            throw new RuntimeException("Invalid number of arguments");
        }
        outputFile = new File(parameters.get(0));

        String target = parameters.get(1);
        bucket = S3PathUtils.getBucket(target);
        key = S3PathUtils.getPrefix(target);
        progress = progress == null;

        // infer name from passed in file if it's not specified in the s3:// String
        if (key == null) {
            key = outputFile.getName();
        }

        // TODO validate params here
        try {
            if (progress) {
                progressListener = new PrintingProgressListener(output);
            }

            new Put(amazonS3Client, bucket, key, outputFile)
                    .withProgressListener(progressListener)
                    .call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        if (!help) {
            try {
                new Put(amazonS3Client, bucket, key, outputFile)
                        .withProgressListener(progressListener)
                        .call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
