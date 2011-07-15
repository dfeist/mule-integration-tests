/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.usecases.routing.response;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.routing.requestreply.AbstractAsyncRequestReplyRequester;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.tck.SensingNullMessageProcessor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResponseAggregatorTestCase extends AbstractServiceAndFlowTestCase
{
    public ResponseAggregatorTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "org/mule/test/usecases/routing/response/response-router-service.xml"},
            {ConfigVariant.FLOW, "org/mule/test/usecases/routing/response/response-router-flow.xml"}});
    }

    @Test
    public void testSyncResponse() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage message = client.send("http://localhost:28081", "request", null);
        assertNotNull(message);
        assertEquals("Received: request", new String(message.getPayloadAsBytes()));
    }

    @Test
    public void testResponseEventsCleanedUp() throws Exception
    {
        RelaxedAsyncReplyMP mp = new RelaxedAsyncReplyMP();

        MuleEvent event = getTestEvent("message1");
        final MuleMessage message = event.getMessage();
        final String id = message.getUniqueId();
        message.setCorrelationId(id);
        message.setCorrelationGroupSize(1);

        SensingNullMessageProcessor listener = getSensingNullMessageProcessor();
        mp.setListener(listener);
        mp.setReplySource(listener.getMessageSource());

        mp.process(event);

        Map<String, MuleEvent> responseEvents = mp.getResponseEvents();
        assertTrue("Response events should be cleaned up.", responseEvents.isEmpty());
    }

    /**
     * This class opens up the access to responseEvents map for testing
     */
    private static final class RelaxedAsyncReplyMP extends AbstractAsyncRequestReplyRequester
    {
        public Map<String, MuleEvent> getResponseEvents()
        {
            return responseEvents;
        }
    }
}
