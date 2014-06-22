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

import com.github.rholder.esthree.command.Ls;
import com.github.rholder.esthree.util.S3PathUtils;
import io.airlift.command.Arguments;
import io.airlift.command.Command;
import io.airlift.command.Option;

import java.math.BigInteger;
import java.util.List;

@Command(name = "ls", description = "List the target bucket with an optional prefix")
public class LsCommand extends EsthreeCommand {

    @Option(name = {"-l", "--limit"}, arity = 1,
            description = "Limit the number of returned results (unlimited by default)")
    public BigInteger limit;

    @Option(name = {"-lf", "--list-format"}, arity = 1, title = "format",
            description = "The list format to use for displaying normal keys, defaulting to \""+ Ls.DEFAULT_LIST_FORMAT +"\"")
    public String listFormat;

    @Option(name = {"-ldf", "--list-directory-format"}, arity = 1, title = "format",
            description = "The list directory format to use for displaying directories, defaulting to \"" + Ls.DEFAULT_LIST_DIR_FORMAT + "\"")
    public String listDirFormat;

    @Arguments(usage = "[target bucket and optional prefix]",
            description = "List the target bucket (with an optional prefix), as in \"s3://bucket\" or \"s3://bucket/prefix\"",
            required = true)
    public List<String> parameters;

    @Override
    public void run() {
        String target = parameters.get(0);
        String bucket = S3PathUtils.getBucket(target);
        String prefix = S3PathUtils.getPrefix(target);
        // TODO validate params here

        try {
            Ls ls = new Ls(amazonS3Client, bucket)
                    .withPrefix(prefix)
                    .withLimit(limit)
                    .withPrintStream(output);

            if (listFormat != null) {
                ls.withListFormat(listFormat);
            }

            if(listDirFormat != null) {
                ls.withListDirFormat(listDirFormat);
            }

            // TODO add withListDirFormat customization?

            ls.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
