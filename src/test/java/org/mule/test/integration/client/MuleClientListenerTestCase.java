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

import org.mule.MuleManager;
import org.mule.config.ConfigurationBuilder;
import org.mule.config.builders.QuickConfigurationBuilder;
import org.mule.extras.client.MuleClient;
import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.tck.FunctionalTestCase;
import org.mule.umo.UMOMessage;
import org.mule.umo.provider.NoReceiverForEndpointException;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class MuleClientListenerTestCase extends FunctionalTestCase
{
    protected String getConfigResources() {
        return null;
    }

    protected ConfigurationBuilder getBuilder() throws Exception {
        return new QuickConfigurationBuilder();
    }

    public void doTestRegisterListener(String urlString, boolean canSendWithoutReceiver) throws Exception
    {
        MuleClient client = new MuleClient();
        client.getConfiguration().setSynchronous(true);
        client.getConfiguration().setRemoteSync(true);

        if (!canSendWithoutReceiver) {
            try {
                client.send(urlString, "Test Client Send message", null);
                fail("There is no receiver for this endpointUri");
            } catch (Exception e) {
                assertTrue(e.getCause() instanceof NoReceiverForEndpointException);
            }
        }

        TestReceiver receiver = new TestReceiver();
        MuleEndpointURI url = new MuleEndpointURI(urlString);
        String name = "myComponent";
        client.registerComponent(receiver, name, url);
        assertTrue(MuleManager.getInstance().getModel().isComponentRegistered(name));

        UMOMessage message = client.send(urlString, "Test Client Send message", null);
        assertNotNull(message);
        assertEquals("Received: Test Client Send message", message.getPayloadAsString());
        client.unregisterComponent(name);

        assertTrue(!MuleManager.getInstance().getModel().isComponentRegistered(name));

        if (!canSendWithoutReceiver) {
            try {
                message = client.send(urlString, "Test Client Send message", null);
                fail("There is no receiver for this endpointUri");
            } catch (Exception e) {
                assertTrue(e.getCause() instanceof NoReceiverForEndpointException);
            }
        }
    }

    public void testRegisterListenerVm() throws Exception
    {
        doTestRegisterListener("vm://localhost/test.queue", false);
    }

    public void testRegisterListenerTcp() throws Exception
    {
        doTestRegisterListener("tcp://localhost:56324", true);
    }

}
