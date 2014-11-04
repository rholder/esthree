package com.github.rholder.esthree.cli;

import com.github.rholder.esthree.command.Mb;
import io.airlift.command.Arguments;
import io.airlift.command.Command;
import io.airlift.command.Option;

import java.util.List;

import static com.google.common.base.Objects.firstNonNull;
import static java.util.Collections.emptyList;

@Command(name = "mb", description = "Create a new bucket")
public class MbCommand extends EsthreeCommand {

    @Option(name = {"-r", "--region"}, arity = 1, description = "The region in which to create this bucket")
    public String region;

    @Arguments(description = "Create a new bucket", usage = "[target bucket name]")
    public List<String> parameters;

    public String bucket;

    @Override
    public void parse() {
        if (help) {
            showUsage(commandMetadata);
            return;
        }

        if (firstNonNull(parameters, emptyList()).size() == 0) {
            showUsage(commandMetadata);
            throw new IllegalArgumentException("No arguments specified");
        }

        if (parameters.size() != 1) {
            output.print("Invalid number of arguments");
            throw new RuntimeException("Invalid number of arguments");
        }
        bucket = parameters.get(0);
    }

    @Override
    public void run() {
        if (!help) {
            try {
                new Mb(amazonS3Client, bucket, region).call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
