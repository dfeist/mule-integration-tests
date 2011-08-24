/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.construct;

import static org.junit.Assert.assertEquals;

import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.lifecycle.LifecycleTrackerProcessor;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

public class SubFlowTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/construct/sub-flow.xml";
    }

    @Test
    public void testProcessorChainViaProcessorRef() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage result = client.send("vm://ProcessorChainViaProcessorRef", "", null);
        assertEquals("1xyz2", result.getPayloadAsString());

        assertEquals("[setMuleContext, initialise, setService, setMuleContext, initialise, start]",
            result.getInboundProperty(LifecycleTrackerProcessor.LIFECYCLE_TRACKER_PROCESSOR_PROPERTY));
        assertEquals(muleContext.getRegistry().lookupFlowConstruct("ProcessorChainViaProcessorRef"),
            result.getInboundProperty(LifecycleTrackerProcessor.FLOW_CONSRUCT_PROPERTY));
    }

    @Test
    public void testProcessorChainViaFlowRef() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage result = client.send("vm://ProcessorChainViaFlowRef", "", null);

        assertEquals("1xyz2", result.getPayloadAsString());

        assertEquals("[setMuleContext, initialise, setService, setMuleContext, initialise, start]",
            result.getInboundProperty(LifecycleTrackerProcessor.LIFECYCLE_TRACKER_PROCESSOR_PROPERTY));
        assertEquals(muleContext.getRegistry().lookupFlowConstruct("ProcessorChainViaFlowRef"),
            result.getInboundProperty(LifecycleTrackerProcessor.FLOW_CONSRUCT_PROPERTY));
    }
    
    @Test
    public void testSubFlowViaProcessorRef() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage result = client.send("vm://SubFlowViaProcessorRef", "", null);
        assertEquals("1xyz2", result.getPayloadAsString());

        assertEquals("[setMuleContext, initialise, setService, setMuleContext, initialise, start]",
            result.getInboundProperty(LifecycleTrackerProcessor.LIFECYCLE_TRACKER_PROCESSOR_PROPERTY));
        assertEquals(muleContext.getRegistry().lookupFlowConstruct("SubFlowViaProcessorRef"),
            result.getInboundProperty(LifecycleTrackerProcessor.FLOW_CONSRUCT_PROPERTY));
    }

    @Test
    public void testSubFlowViaFlowRef() throws Exception
    {
        MuleClient client = muleContext.getClient();
        MuleMessage result = client.send("vm://SubFlowViaFlowRef", "", null);

        assertEquals("1xyz2", result.getPayloadAsString());

        assertEquals("[setMuleContext, initialise, setService, setMuleContext, initialise, start]",
            result.getInboundProperty(LifecycleTrackerProcessor.LIFECYCLE_TRACKER_PROCESSOR_PROPERTY));
        assertEquals(muleContext.getRegistry().lookupFlowConstruct("SubFlowViaFlowRef"),
            result.getInboundProperty(LifecycleTrackerProcessor.FLOW_CONSRUCT_PROPERTY));
    }

    @Test
    public void testFlowviaFlowRef() throws Exception
    {
        MuleClient client = muleContext.getClient();
        assertEquals("1xyz2", client.send("vm://FlowViaFlowRef", "", null).getPayloadAsString());
    }

    @Test
    public void testServiceviaFlowRef() throws Exception
    {
        MuleClient client = muleContext.getClient();
        assertEquals("1xyz2", client.send("vm://ServiceViaFlowRef", "", null).getPayloadAsString());

    }

}
