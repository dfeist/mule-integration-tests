/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.client;

import org.mule.extras.client.MuleClient;
import org.mule.extras.client.RemoteDispatcher;
import org.mule.tck.FunctionalTestCase;
import org.mule.test.integration.service.Person;
import org.mule.umo.UMOMessage;
import org.mule.transformers.xml.wire.XStreamWireFormat;

public class MuleClientRemotingAxisTestCase extends FunctionalTestCase
{
    public MuleClientRemotingAxisTestCase()
    {
        setDisposeManagerPerSuite(true);
    }

    protected String getConfigResources()
    {
        return "org/mule/test/integration/client/axis-client-test-mule-config.xml";
    }

    public void testRequestResponse() throws Throwable
    {
        MuleClient client = new MuleClient();
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("tcp://localhost:38100");
        dispatcher.setWireFormat(new XStreamWireFormat());
        try
        {
            UMOMessage result = dispatcher.sendRemote(
                "axis:http://localhost:38104/mule/services/mycomponent2?method=echo", "test", null);
            assertNotNull(result);
            assertEquals("test", result.getPayloadAsString());
        }
        finally
        {
            client.dispose();
        }
    }

    public void testRequestResponseComplex() throws Exception
    {
        MuleClient client = new MuleClient();
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("tcp://localhost:38100");
        dispatcher.setWireFormat(new XStreamWireFormat());

        try
        {
            UMOMessage result = dispatcher.sendRemote(
                "axis:http://localhost:38104/mule/services/mycomponent3?method=getPerson", "Fred", null);
            assertNotNull(result);
            logger.debug(result.getPayload());
            assertTrue(result.getPayload() instanceof Person);
            assertEquals("Fred", ((Person)result.getPayload()).getFirstName());
            assertEquals("Flintstone", ((Person)result.getPayload()).getLastName());
        }
        finally
        {
            client.dispose();
        }
    }

    public void testRequestResponseComplex2() throws Exception
    {
        MuleClient client = new MuleClient();
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("tcp://localhost:38100");
        dispatcher.setWireFormat(new XStreamWireFormat());
        
        try
        {
            String[] args = new String[]{"Betty", "Rubble"};
            UMOMessage result = dispatcher.sendRemote(
                "axis:http://localhost:38104/mule/services/mycomponent3?method=addPerson", args, null);
            assertNotNull(result);
            assertTrue(result.getPayload() instanceof Person);
            assertEquals("Betty", ((Person)result.getPayload()).getFirstName());
            assertEquals("Rubble", ((Person)result.getPayload()).getLastName());

            // do a receive
            result = client.send("axis:http://localhost:38104/mule/services/mycomponent3?method=getPerson",
                "Betty", null);
            assertNotNull(result);
            assertTrue(result.getPayload() instanceof Person);
            assertEquals("Betty", ((Person)result.getPayload()).getFirstName());
            assertEquals("Rubble", ((Person)result.getPayload()).getLastName());
        }
        finally
        {
            client.dispose();
        }
    }
}
