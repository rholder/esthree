package com.github.rholder.esthree.cli;

import com.github.rholder.esthree.Main;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static com.github.rholder.esthree.TestUtils.expectParseException;

public class GetCommandTest extends GetCommand {

    @Test
    public void defaultRun() {
        EsthreeCommand cmd = new EsthreeCommand() {
            @Override
            public void parse() {
                // no-op
            }
        };
        cmd.run();
    }

    @Test
    public void noParameters() {
        Main main = new Main();
        main.parseGlobalCli("get");
        expectParseException(main.command, "No arguments specified");
    }

    @Test
    public void help() {
        Main main = new Main();
        main.parseGlobalCli("get", "-h");
        main.command.parse();
    }

    @Test
    public void happyPath() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("get", "s3://foo/bar.txt");
        main.command.parse();

        GetCommand c = (GetCommand) main.command;
        Assert.assertTrue(c.progress);
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals("bar.txt", c.key);
    }

    @Test
    public void happyPathNoProgress() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("get", "-np", "s3://foo/bar.txt");
        main.command.parse();

        GetCommand c = (GetCommand) main.command;
        Assert.assertFalse(c.progress);
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals("bar.txt", c.key);
    }

    @Test
    public void happyPathWithTargetFile() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("get", "s3://foo/bar.txt", "baz.txt");
        main.command.parse();

        GetCommand c = (GetCommand) main.command;
        Assert.assertTrue(c.progress);
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals("bar.txt", c.key);
        Assert.assertEquals("baz.txt", c.outputFile.getName());
    }

    @Test
    public void garbagePath() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("get", "potato");

        expectParseException(main.command, "target filename");
    }

    @Test
    public void bucketWithNoFilename() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("get", "s3://foo");

        expectParseException(main.command, "target filename");
    }

    @Test
    public void bucketWithBlankFilename() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("get", "s3://foo/");

        expectParseException(main.command, "target filename");
    }

    @Test
    public void bucketWithSlashGarbage() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("get", "s3://foo/potato/");

        expectParseException(main.command, "target filename");
    }
}
