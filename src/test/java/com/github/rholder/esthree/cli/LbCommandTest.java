package com.github.rholder.esthree.cli;


import com.github.rholder.esthree.Main;
import io.airlift.command.ParseArgumentsUnexpectedException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class LbCommandTest extends LbCommand {

    @Test
    public void noParameters() {
        Main main = new Main();
        main.parseGlobalCli("lb");
        main.command.parse();
    }

    @Test
    public void help() {
        Main main = new Main();
        main.parseGlobalCli("lb", "-h");
        main.command.parse();
    }

    @Test
    public void happyPath() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("lb");
        main.command.parse();

        LbCommand c = (LbCommand) main.command;
        Assert.assertEquals(DEFAULT_LIST_BUCKET_FORMAT, c.listBucketFormat);
    }

    @Test
    public void happyPathWithFormat() throws IOException {
        Main main = new Main();
        main.parseGlobalCli("lb", "-lbf", "%3   %1$tF %1$tR   s3://%2$s");
        main.command.parse();

        LbCommand c = (LbCommand) main.command;
        Assert.assertEquals("%3   %1$tF %1$tR   s3://%2$s", c.listBucketFormat);
    }

    @Test
    public void garbageParameter() throws IOException {
        Main main = new Main();
        try {
            main.parseGlobalCli("lb", "potato");
            Assert.fail("Expected ParseArgumentsUnexpectedException");
        } catch (ParseArgumentsUnexpectedException e) {
            Assert.assertEquals("Found unexpected parameters: [potato]", e.getMessage());
        }
    }
}
