package com.github.rholder.esthree;

import com.github.rholder.esthree.cli.EsthreeCommand;
import org.junit.Assert;

public class TestUtils {

    public static void expectParseException(EsthreeCommand command, String exceptionMessage) {
        try {
            command.parse();
            Assert.fail("Expected Exception");
        } catch(Exception e) {
            Assert.assertTrue(e.getMessage().contains(exceptionMessage));
        }
    }
}
