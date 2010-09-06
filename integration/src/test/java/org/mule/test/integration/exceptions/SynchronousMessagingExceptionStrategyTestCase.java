/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.exceptions;

import org.mule.api.MuleMessage;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class SynchronousMessagingExceptionStrategyTestCase extends AbstractExceptionStrategyTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/exceptions/synch-messaging-exception-strategy.xml";
    }

    public void testInboundTransformer() throws Exception
    {
        client.send("vm://in1", TEST_MESSAGE, null);
        latch.await(LATCH_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS); 
        assertEquals(1, serviceExceptionCounter.get());
        assertEquals(0, systemExceptionCounter.get());
    }
    
    public void testInboundResponseTransformer() throws Exception
    {
        client.send("vm://in2", TEST_MESSAGE, null);
        latch.await(LATCH_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS); 
        assertEquals(1, serviceExceptionCounter.get());
        assertEquals(0, systemExceptionCounter.get());
    }
    
    public void testOutboundTransformer() throws Exception
    {
        client.send("vm://in3", TEST_MESSAGE, null);
        latch.await(LATCH_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS); 
        assertEquals(1, serviceExceptionCounter.get());
        assertEquals(0, systemExceptionCounter.get());
        MuleMessage response = client.request("vm://out3", 500);
        assertNull(response);
    }
    
    public void testOutboundResponseTransformer() throws Exception
    {
        client.send("vm://in4", TEST_MESSAGE, null);
        latch.await(LATCH_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS); 
        assertEquals(1, serviceExceptionCounter.get());
        assertEquals(0, systemExceptionCounter.get());
        MuleMessage response = client.request("vm://out4", 500);
        assertNull(response);
    }
    
    public void testComponent() throws Exception
    {
        client.send("vm://in5", TEST_MESSAGE, null);
        latch.await(LATCH_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS); 
        assertEquals(1, serviceExceptionCounter.get());
        assertEquals(0, systemExceptionCounter.get());
    }

    public void testInboundRouter() throws Exception
    {
        client.send("vm://in6", TEST_MESSAGE, null);
        latch.await(LATCH_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS); 
        assertEquals(1, serviceExceptionCounter.get());
        assertEquals(0, systemExceptionCounter.get());
    }
    
    public void testOutboundRouter() throws Exception
    {
        client.send("vm://in7", TEST_MESSAGE, null);
        latch.await(LATCH_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS); 
        assertEquals(1, serviceExceptionCounter.get());
        assertEquals(0, systemExceptionCounter.get());
        MuleMessage response = client.request("vm://out7", 500);
        assertNull(response);
    }    
}

