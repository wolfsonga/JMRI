package jmri.jmrix.marklin;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * MarklinTrafficControllerTest.java
 *
 * Description:	tests for the jmri.jmrix.marklin.MarklinTrafficController class
 *
 * @author	Paul Bender Copyright (C) 2012,2016
 */
public class MarklinTrafficControllerTest {

    @Test
    public void testCtor() {
        MarklinTrafficController c = new MarklinTrafficController();
        Assert.assertNotNull(c);
    }

    // The minimal setup for log4J
    @Before
    public void setUp() {
        apps.tests.Log4JFixture.setUp();
    }

    @After
    public void tearDown() {
        apps.tests.Log4JFixture.tearDown();
    }

}
