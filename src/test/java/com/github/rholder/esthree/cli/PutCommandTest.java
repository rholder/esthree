package com.github.rholder.esthree.cli;

import com.github.rholder.esthree.Main;
import org.junit.Test;

import java.io.IOException;

import static com.github.rholder.esthree.TestUtils.expectParseException;

public class PutCommandTest {

    @Test
    public void noParameters() {
        Main main = new Main();
        main.parseGlobalCli("put");
        expectParseException(main.command, "No arguments specified");
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
    }

    @Test
    public void happyPathBucketSlash() {
        Main main = new Main();
        main.parseGlobalCli("put", "beep", "s3://foo/");
        main.command.parse();
    }

    @Test
    public void happyPathFile() {
        Main main = new Main();
        main.parseGlobalCli("put", "beep", "s3://foo/boop");
        main.command.parse();
    }

    @Test
    public void happyPathNoProgress() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("put", "-np", "beep", "s3://foo/bar.txt");
        main.command.parse();
    }

    /*
    @Test
    public void happyPathWithTargetFile() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("get", "s3://foo/bar.txt", "baz.txt");
        main.command.parse();
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
    */
}
