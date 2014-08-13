package com.github.rholder.esthree.cli;

import com.github.rholder.esthree.Main;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static com.github.rholder.esthree.TestUtils.expectParseException;

public class PutCommandTest extends PutCommand {

    @Test
    public void noParameters() {
        Main main = new Main();
        main.parseGlobalCli("put");
        expectParseException(main.command, "No arguments specified");
    }

    @Test
    public void missingParameters() {
        Main main = new Main();
        main.parseGlobalCli("put", "s3://foo/");
        expectParseException(main.command, "Invalid number of arguments");
    }

    @Test
    public void help() {
        Main main = new Main();
        main.parseGlobalCli("put", "-h");
        main.command.parse();
    }

    @Test
    public void happyPathBucket() {
        Main main = new Main();
        main.parseGlobalCli("put", "beep", "s3://foo");
        main.command.parse();

        PutCommand c = (PutCommand) main.command;
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals("beep", c.key);
    }

    @Test
    public void happyPathBucketSlash() {
        Main main = new Main();
        main.parseGlobalCli("put", "beep", "s3://foo/");
        main.command.parse();

        PutCommand c = (PutCommand) main.command;
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals("beep", c.key);
    }

    @Test
    public void happyPathBucketTrailingSlash() {
        Main main = new Main();
        main.parseGlobalCli("put", "beep", "s3://foo/bar/");
        main.command.parse();

        PutCommand c = (PutCommand) main.command;
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals("bar/beep", c.key);
    }

    @Test
    public void happyPathFile() {
        Main main = new Main();
        main.parseGlobalCli("put", "beep", "s3://foo/boop");
        main.command.parse();

        PutCommand c = (PutCommand) main.command;
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals("boop", c.key);
    }

    @Test
    public void happyPathNoProgress() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("put", "-np", "beep", "s3://foo/bar.txt");
        main.command.parse();

        PutCommand c = (PutCommand) main.command;
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals("bar.txt", c.key);
    }

    @Test
    public void garbagePath() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("put", "banana", "potato");

        expectParseException(main.command, "Could not parse bucket name");
    }
}
