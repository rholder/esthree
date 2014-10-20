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

import com.github.rholder.esthree.command.Lb;
import io.airlift.command.Command;
import io.airlift.command.Option;

@Command(name = "lb", description = "List all available buckets")
public class LbCommand extends EsthreeCommand {

    // that's pretty close to s3cmd
    public static final String DEFAULT_LIST_BUCKET_FORMAT = "%1$tF %1$tR   s3://%2$s";

    @Option(name = {"-lbf", "--list-bucket-format"}, arity = 1, title = "format",
            description = "The list bucket format to use for displaying buckets, defaulting to \"" + DEFAULT_LIST_BUCKET_FORMAT + "\"")
    public String listBucketFormat;

    @Override
    public void parse() {
        if (help) {
            showUsage(commandMetadata);
            return;
        }

        listBucketFormat = listBucketFormat == null ? DEFAULT_LIST_BUCKET_FORMAT : listBucketFormat;
    }

    @Override
    public void run() {
        if (!help) {
            try {
                Lb lb = new Lb(amazonS3Client)
                        .withListBucketFormat(listBucketFormat)
                        .withPrintStream(output);

                lb.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
