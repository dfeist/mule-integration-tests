/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.client;

import org.mule.RegistryContext;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.jms.JmsConstants;

import java.util.HashMap;
import java.util.Map;

public class MuleClientJmsTestCase extends FunctionalTestCase
{
    public static final int INTERATIONS = 1;

    public MuleClientJmsTestCase()
    {
        setDisposeManagerPerSuite(true);
    }

    protected String getConfigResources()
    {
        return "org/mule/test/integration/client/test-client-jms-mule-config.xml";
    }

    public void testClientSendDirect() throws Exception
    {
        MuleClient client = new MuleClient();
        RegistryContext.getConfiguration().setDefaultSynchronousEndpoints(true);

        MuleMessage message = client.sendDirect("TestReceiverUMO", null, "Test Client Send message", null);
        assertNotNull(message);
        assertEquals("Received: Test Client Send message", message.getPayload());
    }

    public void testClientDispatchDirect() throws Exception
    {
        MuleClient client = new MuleClient();
        RegistryContext.getConfiguration().setDefaultSynchronousEndpoints(true);

        client.dispatchDirect("TestReceiverUMO", "Test Client dispatch message", null);
    }

    public void testClientSend() throws Exception
    {
        MuleClient client = new MuleClient();
        RegistryContext.getConfiguration().setDefaultSynchronousEndpoints(true);
        RegistryContext.getConfiguration().setDefaultRemoteSync(true);

        MuleMessage message = client.send(getDispatchUrl(), "Test Client Send message", null);
        assertNotNull(message);
        assertEquals("Received: Test Client Send message", message.getPayload());
    }

    public void testClientMultiSend() throws Exception
    {
        MuleClient client = new MuleClient();
        RegistryContext.getConfiguration().setDefaultSynchronousEndpoints(true);
        RegistryContext.getConfiguration().setDefaultRemoteSync(true);

        for (int i = 0; i < INTERATIONS; i++)
        {
            MuleMessage message = client.send(getDispatchUrl(), "Test Client Send message " + i, null);
            assertNotNull(message);
            assertEquals("Received: Test Client Send message " + i, message.getPayload());
        }
    }

    public void testClientMultiDispatch() throws Exception
    {
        MuleClient client = new MuleClient();
        RegistryContext.getConfiguration().setDefaultSynchronousEndpoints(false);

        int i = 0;
        // to init
        client.dispatch(getDispatchUrl(), "Test Client Send message " + i, null);
        long start = System.currentTimeMillis();
        for (i = 0; i < INTERATIONS; i++)
        {
            client.dispatch(getDispatchUrl(), "Test Client Send message " + i, null);
        }
        long time = System.currentTimeMillis() - start;
        logger.debug(i + " took " + time + "ms to process");
        Thread.sleep(1000);
    }

    public void testClientDispatchAndReceiveOnReplyTo() throws Exception
    {
        MuleClient client = new MuleClient();
        RegistryContext.getConfiguration().setDefaultSynchronousEndpoints(false);

        Map props = new HashMap();
        props.put(JmsConstants.JMS_REPLY_TO, "replyTo.queue");

        long start = System.currentTimeMillis();
        int i = 0;
        for (i = 0; i < INTERATIONS; i++)
        {
            logger.debug("Sending message " + i);
            client.dispatch(getDispatchUrl(), "Test Client Dispatch message " + i, props);
        }
        long time = System.currentTimeMillis() - start;
        logger.debug("It took " + time + " ms to send " + i + " messages");

        Thread.sleep(5000);
        start = System.currentTimeMillis();
        for (i = 0; i < INTERATIONS; i++)
        {
            MuleMessage message = client.request("jms://replyTo.queue", 5000);
            assertNotNull("message should not be null from Reply queue", message);
            logger.debug("Count is " + i);
            logger.debug("ReplyTo Message is: " + message.getPayloadAsString());
            assertTrue(message.getPayloadAsString().startsWith("Received"));
        }
        time = System.currentTimeMillis() - start;
        logger.debug("It took " + time + "ms to receive " + i + " messages");
    }

    public String getDispatchUrl()
    {
        return "jms://test.queue";
    }
}
