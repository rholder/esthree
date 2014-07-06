package com.github.rholder.esthree.cli;

import com.amazonaws.services.s3.AmazonS3Client;
import io.airlift.command.CommandUsage;
import io.airlift.command.Option;
import io.airlift.command.OptionType;
import io.airlift.command.model.CommandMetadata;

import java.io.PrintStream;

public abstract class EsthreeCommand implements Runnable {

    public CommandMetadata commandMetadata;
    public AmazonS3Client amazonS3Client;
    public PrintStream output;

    @Option(name = {"-h", "-help", "--help"}, description = "Common aliases for getting help", hidden = true)
    public Boolean help = false;

    @Option(name = "-v", description = "Verbose mode, show full stacktraces on errors")
    public boolean verbose;

    @Option(type = OptionType.GLOBAL, name = "--aws-access-key-id", arity = 1, description = "The AWS Access Key Id to use for making requests")
    public String accessKey;

    @Option(type = OptionType.GLOBAL, name = "--aws-secret-access-key", arity = 1, description = "The AWS Secret Access Key to use for making requests")
    public String secretKey;

    @Option(type = OptionType.GLOBAL, name = "--endpoint", arity = 1, description = "The AWS S3 endpoint, defaults to s3.amazonaws.com, but could be something like s3.s3-us-west-1.amazonaws.com")
    public String endpoint;

    public abstract void parse();

    public void run() {
        System.out.println(getClass().getSimpleName());
    }

    public void showUsage(CommandMetadata commandMetadata) {
        StringBuilder out = new StringBuilder();
        new CommandUsage().usage(null, null, commandMetadata.getName(), commandMetadata, out);
        output.println(out.toString());
    }
}
