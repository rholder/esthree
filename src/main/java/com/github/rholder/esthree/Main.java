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

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.github.rholder.esthree.cli.EsthreeCommand;
import com.github.rholder.esthree.cli.GetCommand;
import com.github.rholder.esthree.cli.GetMultipartCommand;
import com.github.rholder.esthree.cli.HelpCommand;
import com.github.rholder.esthree.cli.LsCommand;
import com.github.rholder.esthree.cli.PutCommand;
import io.airlift.command.Cli;
import io.airlift.command.model.MetadataLoader;

public class Main {

    public static final String HEADER = "%s - An S3 client that just works";

    public EsthreeCommand command;

    public static void main(String... args) {
        new Main().execute(args);
    }

    public static String getVersion() {
        // TODO pull this from the manifest
        return "0.2.2-SNAPSHOT";
    }

    @SuppressWarnings("unchecked")
    public Cli<EsthreeCommand> createCli() {
        return Cli.<EsthreeCommand>builder("esthree")
                .withDescription(String.format(HEADER, getVersion()))
                .withDefaultCommand(HelpCommand.class)
                .withCommands(HelpCommand.class, GetCommand.class, GetMultipartCommand.class, LsCommand.class, PutCommand.class)
                .build();
    }

    public void parseGlobalCli(String... args) {
        command = createCli().parse(args);
        command.commandMetadata = MetadataLoader.loadCommand(command.getClass());
        command.output = System.out;

        // override if keys are specified
        if(command.accessKey != null || command.secretKey != null) {
            command.amazonS3Client = new AmazonS3Client(new BasicAWSCredentials(command.accessKey, command.secretKey));
        } else {
            command.amazonS3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
        }

        // override S3 endpoint if specified
        if(command.endpoint != null) {
            command.amazonS3Client.setEndpoint(command.endpoint);
        }
    }

    public void execute(String... args) {
        try {
            parseGlobalCli(args);

            command.parse();
            command.run();
        } catch (Exception e) {
            if(command != null && command.verbose) {
                e.printStackTrace();
            } else {
                System.out.println(e.getMessage());
            }
            System.exit(1);
        }
    }
}
