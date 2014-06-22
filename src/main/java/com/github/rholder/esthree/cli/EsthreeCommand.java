package com.github.rholder.esthree.cli;

import com.amazonaws.services.s3.AmazonS3Client;
import io.airlift.command.HelpOption;
import io.airlift.command.Option;
import io.airlift.command.OptionType;

import javax.inject.Inject;
import java.io.PrintStream;

public class EsthreeCommand implements Runnable {

    public AmazonS3Client amazonS3Client;
    public PrintStream output;

    // TODO add target cloud provider, default to AWS

    @Inject
    public HelpOption helpOption;

    //@Option(type = OptionType.GLOBAL, name = {"-h", "-help", "--help"}, description = "Common aliases for getting help", hidden = true)
    //public Boolean help;

    // TODO print full stack trace
    @Option(name = "-v", description = "Verbose mode, show full stacktraces on errors")
    public boolean verbose;

    @Option(type = OptionType.GLOBAL, name = "--aws-access-key-id", arity = 1, description = "The AWS Access Key Id to use for making requests")
    public String accessKey;

    @Option(type = OptionType.GLOBAL, name = "--aws-secret-access-key", arity = 1, description = "The AWS Secret Access Key to use for making requests")
    public String secretKey;

    public void run() {
        System.out.println(getClass().getSimpleName());
    }
}
