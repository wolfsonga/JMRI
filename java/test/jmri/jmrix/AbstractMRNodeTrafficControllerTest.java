package jmri.jmrix;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ResourceBundle;
import jmri.InstanceManager;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for AbstractMRNodeTrafficController.
 * @author Paul Bender Copyright (C) 2016
 */
public class AbstractMRNodeTrafficControllerTest extends AbstractMRTrafficControllerTest {
    
    @Test
    public void testCtor() {
        Assert.assertNotNull(tc);
    }

    @Test
    public void testGetNumNodesZero(){
        Assert.assertEquals("NumNodes at start",0,((AbstractMRNodeTrafficController)tc).getNumNodes());
    }

    @Before
    public void setUp() {
        apps.tests.Log4JFixture.setUp(); 
        JUnitUtil.resetInstanceManager();
        tc = new AbstractMRNodeTrafficController(){
           @Override
           protected void setInstance() {};
           @Override
           protected void forwardMessage(AbstractMRListener client, AbstractMRMessage m){
           }
           @Override
           protected AbstractMRMessage pollMessage(){ return null; }
           @Override
           protected AbstractMRListener pollReplyHandler() { return null; }
           @Override
           protected AbstractMRMessage enterProgMode() { return null; }
           @Override
           protected AbstractMRMessage enterNormalMode() { return null; }
           @Override
           protected void forwardReply(AbstractMRListener client, AbstractMRReply m){}
           @Override
           protected AbstractMRReply newReply() { return null; }
           @Override
           protected boolean endOfMessage(AbstractMRReply r) {return true; }
        };
    }

    @After
    public void tearDown(){
       tc = null;
        JUnitUtil.resetInstanceManager();
        apps.tests.Log4JFixture.tearDown(); 
    }

}
