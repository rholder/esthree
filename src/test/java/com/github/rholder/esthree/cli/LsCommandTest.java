package com.github.rholder.esthree.cli;


import com.github.rholder.esthree.Main;
import org.junit.Test;

import java.io.IOException;

import static com.github.rholder.esthree.TestUtils.expectParseException;

public class LsCommandTest {

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
    }

    @Test
    public void happyPathWithSlash() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("ls", "s3://foo/");
        main.command.parse();
    }

    @Test
    public void happyPathWithPrefix() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("ls", "s3://foo/bar");
        main.command.parse();
    }

    @Test
    public void garbagePath() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("ls", "potato");

        expectParseException(main.command, "Could not determine target bucket");
    }
}
