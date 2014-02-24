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
import com.beust.jcommander.JCommander;
import com.github.rholder.esthree.cli.Command;
import com.github.rholder.esthree.cli.GetCommand;
import com.github.rholder.esthree.cli.GetMultipartCommand;
import com.github.rholder.esthree.cli.LsCommand;
import com.github.rholder.esthree.cli.PutCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static final String HEADER = "esthree %s - An S3 client that just works.\n\n";

    public static final AmazonS3Client AMAZON_S3_CLIENT = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());

    public static final List<Command> COMMAND_LIST = new ArrayList<Command>() {{
        add(new LsCommand(AMAZON_S3_CLIENT, System.out));
        add(new GetCommand(AMAZON_S3_CLIENT, System.out));
        add(new PutCommand(AMAZON_S3_CLIENT, System.out));
        add(new GetMultipartCommand(AMAZON_S3_CLIENT, System.out));
    }};

    public static void main(String[] args) throws Exception {

        JCommander jc = new JCommander();
        jc.setProgramName("esthree");

        // load up all the available commands
        Map<String, Command> commands = new HashMap<String, Command>();
        for(Command c : COMMAND_LIST) {
            jc.addCommand(c);
            commands.put(c.getName(), c);
        }
        jc.parse(args);

        // dispatch and run a command
        Command command = commands.get(jc.getParsedCommand());
        if (command != null) {
            int ret = command.execute();
            System.exit(ret);
        } else {
            // TODO this looks awful, just print the general help manually
            StringBuilder usage = new StringBuilder(String.format(HEADER, getVersion()));
            jc.usage(usage);
            System.out.println(usage);
            System.exit(1);
        }
    }

    public static String getVersion() {
        // TODO pull this from the manifest
        return "0.1.1";
    }
}
