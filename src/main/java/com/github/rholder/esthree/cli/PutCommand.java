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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.github.rholder.esthree.command.Put;
import com.github.rholder.esthree.util.PrintingProgressListener;
import com.github.rholder.esthree.util.S3PathUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

@Parameters(commandNames = {PutCommand.NAME}, separators = " ")
public class PutCommand implements Command {

    public static final String NAME = "put";

    public PrintStream output;

    public PutCommand(PrintStream output) {
        this.output = output;
    }

    @Parameter(names = {"-np", "--no-progress"}, description = "Don't print a progress bar")
    public Boolean progress;

    @Parameter(description = "[filename] [target bucket and key]\n      Upload a file to S3 with the target bucket and optionally the key, as in \"foo.txt s3://bucket/foo.html\"", required = true, arity = 1)
    public List<String> parameters;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int execute() {

        // TODO foo s3://bucket  <--- support this?
        if(parameters.size() != 2) {
            output.print("Invalid number of arguments");
            return 1;
        }
        File outputFile = new File(parameters.get(0));

        String target = parameters.get(1);
        String bucket = S3PathUtils.getBucket(target);
        String key = S3PathUtils.getPrefix(target);
        progress = progress == null;

        // infer name from passed in file if it's not specified in the s3:// String
        if(key == null) {
            key = outputFile.getName();
        }

        // TODO validate params here
        try {

            PrintingProgressListener progressListener = null;
            if(progress) {
                progressListener = new PrintingProgressListener(output);
            }

            return new Put(bucket, key, outputFile)
                    .withProgressListener(progressListener)
                    .call();
        } catch (Exception e) {
            // TODO print message to stderr?
            e.printStackTrace();
            return 1;
        }
    }
}
