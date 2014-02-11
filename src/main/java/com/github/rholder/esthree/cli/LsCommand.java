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
import com.github.rholder.esthree.command.Ls;
import com.github.rholder.esthree.util.BigIntegerConverter;
import com.github.rholder.esthree.util.S3PathUtils;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.List;

@Parameters(commandNames = {LsCommand.NAME}, separators = " ")
public class LsCommand implements Command {

    public static final String NAME = "ls";

    public PrintStream output;

    public LsCommand(PrintStream output) {
        this.output = output;
    }

    @Parameter(names = {"-l", "--limit"}, description = "Limit the number of returned results (unlimited by default)", converter = BigIntegerConverter.class)
    public BigInteger limit;

    @Parameter(names = {"-lf", "--list-format"}, description = "The list format to use")
    public String listFormat;

    @Parameter(description = "[target bucket and key]\n      List the target bucket (with an optional prefix), as in \"s3://bucket\" or \"s3://bucket/prefix\"", required = true, arity = 1)
    public List<String> parameters;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int execute() {
        String target = parameters.get(0);
        String bucket = S3PathUtils.getBucket(target);
        String prefix = S3PathUtils.getPrefix(target);
        // TODO validate params here

        try {
            Ls ls = new Ls(bucket)
                    .withPrefix(prefix)
                    .withLimit(limit)
                    .withPrintStream(output);

            if(listFormat != null) {
                ls.withListFormat(listFormat);
            }

            return ls.call();
        } catch (Exception e) {
            // TODO print message to stderr?
            e.printStackTrace();
            return 1;
        }
    }
}
