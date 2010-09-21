/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.client;

import org.mule.module.client.remoting.RemoteDispatcherAgent;
import org.mule.tck.FunctionalTestCase;
import org.mule.transformer.wire.SerializedMuleMessageWireFormat;

public class RemoteDispatcherAgentConfigTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "org/mule/test/integration/client/mule-admin-agent.xml";
    }

    public void testNonEmptyProperties() throws Exception
    {
        RemoteDispatcherAgent agent = (RemoteDispatcherAgent) muleContext.getRegistry().lookupAgent("remote-dispatcher-agent");
        assertNotNull(agent.getEndpoint());
        assertEquals("test://12345",agent.getEndpoint().getEndpointURI().toString());
        assertNotNull(agent.getWireFormat());
        assertTrue(agent.getWireFormat() instanceof SerializedMuleMessageWireFormat);
    }
    
}
