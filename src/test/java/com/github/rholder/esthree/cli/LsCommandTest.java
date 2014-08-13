package com.github.rholder.esthree.cli;


import com.github.rholder.esthree.Main;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static com.github.rholder.esthree.TestUtils.expectParseException;

public class LsCommandTest extends LsCommand {

    @Test
    public void noParameters() {
        Main main = new Main();
        main.parseGlobalCli("ls");
        expectParseException(main.command, "No arguments specified");
    }

    @Test
    public void help() {
        Main main = new Main();
        main.parseGlobalCli("ls", "-h");
        main.command.parse();
    }

    @Test
    public void happyPath() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("ls", "s3://foo");
        main.command.parse();

        LsCommand c = (LsCommand) main.command;
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals(DEFAULT_LIST_FORMAT, c.listFormat);
        Assert.assertEquals(DEFAULT_LIST_DIR_FORMAT, c.listDirFormat);
        Assert.assertNull(c.limit);
        Assert.assertNull(c.prefix);
    }

    @Test
    public void happyPathWithSlash() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("ls", "s3://foo/");
        main.command.parse();

        LsCommand c = (LsCommand) main.command;
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals(DEFAULT_LIST_FORMAT, c.listFormat);
        Assert.assertEquals(DEFAULT_LIST_DIR_FORMAT, c.listDirFormat);
        Assert.assertNull(c.limit);
        Assert.assertNull(c.prefix);
    }

    @Test
    public void happyPathWithPrefix() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("ls", "s3://foo/bar");
        main.command.parse();

        LsCommand c = (LsCommand) main.command;
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals(DEFAULT_LIST_FORMAT, c.listFormat);
        Assert.assertEquals(DEFAULT_LIST_DIR_FORMAT, c.listDirFormat);
        Assert.assertNull(c.limit);
        Assert.assertEquals("bar", c.prefix);
    }

    @Test
    public void garbagePath() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("ls", "potato");

        expectParseException(main.command, "Could not determine target bucket");
    }
}
