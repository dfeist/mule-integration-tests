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

import org.mule.api.ExceptionPayload;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.module.client.RemoteDispatcher;
import org.mule.tck.FunctionalTestCase;

/**
 * Test remote dispatcher using serialization wire format
 */
public class RemoteDispatcherTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/construct/remote-dispatcher.xml";
    }

    public void testRemoting() throws Exception
    {
        String[] targets = {"service1", "nosuch", "vmConnector", "flow1"};
        String expectedResponses[] = { "Hellogoodbye", null, null, "Helloaloha"};

        MuleClient client = new MuleClient(muleContext);
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("http://localhost:10081");
        for (int i = 0; i < targets.length; i++)
        {
            String construct = targets[i];
            String expected = expectedResponses[i];

            MuleMessage result = dispatcher.sendToRemoteComponent(construct, "Hello", null);

            assertNotNull(result);
            if (expected != null)
            {
                assertEquals(expected, result.getPayload());
            }
            else
            {
                ExceptionPayload payload = result.getExceptionPayload();
                assertNotNull(payload);
                assertTrue(payload.getException().getMessage().contains(construct));
            }
        }
    }
}
