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

import io.airlift.command.Arguments;
import io.airlift.command.Command;
import io.airlift.command.CommandGroupUsage;
import io.airlift.command.CommandUsage;
import io.airlift.command.GlobalUsage;
import io.airlift.command.model.CommandGroupMetadata;
import io.airlift.command.model.CommandMetadata;
import io.airlift.command.model.GlobalMetadata;

import javax.inject.Inject;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Command(name = "help", description = "Display help information, such as \"help get\"", hidden = true)
public class HelpCommand extends EsthreeCommand {

    @Inject
    public GlobalMetadata global;

    @Arguments
    public List<String> command = newArrayList();

    @Override
    public void parse() {
        // nothing to see here
    }

    @Override
    public void run() {
        help(global, command);
    }

    public static void help(GlobalMetadata global, List<String> commandNames) {
        StringBuilder stringBuilder = new StringBuilder();
        help(global, commandNames, stringBuilder);
        System.out.println(stringBuilder.toString());
    }

    public static void help(GlobalMetadata global, List<String> commandNames, StringBuilder out) {
        if (commandNames.isEmpty()) {
            // TODO add header here?
            new GlobalUsage().usage(global, out);
            return;
        }

        String name = commandNames.get(0);

        // main program?
        if (name.equals(global.getName())) {
            new GlobalUsage().usage(global, out);
            return;
        }

        // command in the default group?
        for (CommandMetadata command : global.getDefaultGroupCommands()) {
            if (name.equals(command.getName())) {
                new CommandUsage().usage(global.getName(), null, command.getName(), command, out);
                return;
            }
        }

        // command in a group?
        for (CommandGroupMetadata group : global.getCommandGroups()) {
            if (name.endsWith(group.getName())) {
                // general group help or specific command help?
                if (commandNames.size() == 1) {
                    new CommandGroupUsage().usage(global, group, out);
                    return;
                } else {
                    String commandName = commandNames.get(1);
                    for (CommandMetadata command : group.getCommands()) {
                        if (commandName.equals(command.getName())) {
                            new CommandUsage().usage(global.getName(), group.getName(), command.getName(), command, out);
                            return;
                        }
                    }
                    System.out.println("Unknown command " + name + " " + commandName);
                }
            }
        }

        System.out.println("Unknown command " + name);
    }

}
