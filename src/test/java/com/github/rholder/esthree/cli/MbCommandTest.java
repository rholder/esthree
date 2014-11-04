package com.github.rholder.esthree.cli;

import com.github.rholder.esthree.Main;
import io.airlift.command.ParseOptionMissingValueException;
import org.junit.Assert;
import org.junit.Test;

import static com.github.rholder.esthree.TestUtils.expectParseException;

public class MbCommandTest extends MbCommand {

    @Test
    public void noParameters() {
        Main main = new Main();
        main.parseGlobalCli("mb");
        expectParseException(main.command, "No arguments specified");
    }

    @Test
    public void missingParameters() {
        Main main = new Main();
        try {
            main.parseGlobalCli("mb", "-r");
            Assert.fail("Expected ParseOptionMissingValueException");
        } catch (ParseOptionMissingValueException e) {
            Assert.assertTrue(e.getMessage().equals("Required values for option 'region' not provided"));
        }
    }

    @Test
    public void help() {
        Main main = new Main();
        main.parseGlobalCli("mb", "-h");
        main.command.parse();
    }

    @Test
    public void happyPathBucket() {
        Main main = new Main();
        main.parseGlobalCli("mb", "beep");
        main.command.parse();

        MbCommand c = (MbCommand) main.command;
        Assert.assertEquals("beep", c.bucket);
    }

    @Test
    public void happyPathBucketRegion() {
        Main main = new Main();
        main.parseGlobalCli("mb", "--region", "meep", "foo");
        main.command.parse();

        MbCommand c = (MbCommand) main.command;
        Assert.assertEquals("foo", c.bucket);
        Assert.assertEquals("meep", c.region);
    }
}