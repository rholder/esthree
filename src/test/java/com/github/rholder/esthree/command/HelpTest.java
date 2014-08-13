package com.github.rholder.esthree.command;

import com.github.rholder.esthree.Main;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

public class HelpTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void helpNoneMain() {
        Main.main();
    }

    @Test
    public void helpMain() {
        Main.main("help");
    }

    @Test
    public void helpGetMain() {
        Main.main("help", "get");
    }

    @Test
    public void helpSelfMain() {
        Main.main("help", "esthree");
    }

    @Test
    public void helpUnexpectedMain() {
        exit.expectSystemExitWithStatus(1);
        Main.main("potato");
    }
}
