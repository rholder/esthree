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
import com.github.rholder.esthree.command.GetMultipart;
import com.github.rholder.esthree.util.PrintingProgressListener;
import com.github.rholder.esthree.util.S3PathUtils;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.getPrefixLength;

@Parameters(commandNames = {GetMultipartCommand.NAME}, separators = " ")
public class GetMultipartCommand implements Command {

    public static final String NAME = "get-multi";

    public PrintStream output;

    public GetMultipartCommand(PrintStream output) {
        this.output = output;
    }

    @Parameter(names = {"-c", "--chunk-size"}, description = "The request chunk size in bytes (e.g. 10485760 for 10MB chunks), defaults to 5MB")
    public Integer chunkSize;

    @Parameter(names = {"-np", "--no-progress"}, description = "Don't print a progress bar")
    public Boolean progress;

    @Parameter(description = "[target bucket and key]\n      Download a file from S3 with the target bucket and key, as in \"s3://bucket/foo.html\"", required = true, arity = 1)
    public List<String> parameters;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int execute() {
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
            PrintingProgressListener progressListener = null;
            if(progress) {
                progressListener = new PrintingProgressListener(output);
            }

            return new GetMultipart(bucket, key, outputFile)
                    .withChunkSize(chunkSize)
                    .withProgressListener(progressListener)
                    .call();
        } catch (Exception e) {
            // TODO print message to stderr?
            e.printStackTrace();
            return 1;
        }
    }
}
