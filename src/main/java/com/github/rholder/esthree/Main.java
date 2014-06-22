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

package com.github.rholder.esthree;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.github.rholder.esthree.cli.EsthreeCommand;
import com.github.rholder.esthree.cli.GetCommand;
import com.github.rholder.esthree.cli.GetMultipartCommand;
import com.github.rholder.esthree.cli.HelpCommand;
import com.github.rholder.esthree.cli.LsCommand;
import com.github.rholder.esthree.cli.PutCommand;
import io.airlift.command.Cli;

public class Main {

    public static final String HEADER = "esthree %s - An S3 client that just works.\n\n";

    public static String getVersion() {
        // TODO pull this from the manifest
        return "0.2.0-SNAPSHOT";
    }

    @SuppressWarnings("unchecked")
    public static void main(String... args) {
        Cli<Runnable> a = Cli.<Runnable>builder("esthree")
                .withDescription(String.format(HEADER, getVersion()))
                .withDefaultCommand(HelpCommand.class)
                .withCommands(HelpCommand.class, GetCommand.class, GetMultipartCommand.class, LsCommand.class, PutCommand.class)
                .build();

        try {
            Runnable b = a.parse(args);
            if(b instanceof EsthreeCommand) {
                EsthreeCommand c = (EsthreeCommand)b;
                c.amazonS3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
                c.output = System.out;
            }
            b.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
