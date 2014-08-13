package com.github.rholder.esthree.cli;

import com.github.rholder.esthree.Main;
import io.airlift.command.ParseArgumentsUnexpectedException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class HelpCommandTest extends HelpCommand {

    @Test
    public void helpNone() {
        Main main = new Main();
        main.parseGlobalCli();
        main.command.parse();
        main.command.run();
    }

    @Test
    public void help() {
        Main main = new Main();
        main.parseGlobalCli("help");
        main.command.parse();
        main.command.run();
    }

    @Test
    public void helpGet() {
        Main main = new Main();
        main.parseGlobalCli("help", "get");
        main.command.parse();
        main.command.run();
    }

    @Test
    public void helpSelf() {
        Main main = new Main();
        main.parseGlobalCli("help", "esthree");
        main.command.parse();
        main.command.run();
    }

    @Test
    public void helpUnexpected() {
        try {
            Main main = new Main();
            main.parseGlobalCli("potato");
            Assert.fail("Expected ParseArgumentsUnexpectedException");
        } catch (ParseArgumentsUnexpectedException e) {
            Assert.assertTrue(e.getMessage().contains("Found unexpected parameters"));
        }
    }
}
