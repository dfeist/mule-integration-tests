/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.client;

import org.mule.MuleManager;
import org.mule.extras.client.MuleClient;
import org.mule.extras.client.RemoteDispatcher;
import org.mule.tck.FunctionalTestCase;
import org.mule.umo.UMOMessage;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class MuleClientRemotingJmsTestCase extends FunctionalTestCase
{
    public MuleClientRemotingJmsTestCase() {
        setDisposeManagerPerSuite(true);
    }

    protected String getConfigResources() {
        return "org/mule/test/integration/client/test-client-mule-config-remote-jms.xml";
    }

    public void testClientSendToRemoteComponent() throws Exception
    {
        // Will connect to the server using tcp://localhost:60504
        MuleClient client = new MuleClient();
        MuleManager.getConfiguration().setSynchronous(true);

        RemoteDispatcher dispatcher = client.getRemoteDispatcher(getServerUrl());
        UMOMessage message = dispatcher.sendToRemoteComponent("TestReceiverUMO", "Test Client Send message", null);
        assertNotNull(message);
        assertEquals("Received: Test Client Send message", message.getPayload());
    }

    public void testClientSendAndReceiveRemote() throws Exception
    {
        String remoteEndpoint = "vm://vmRemoteProvider/remote.queue";
        // Will connect to the server using tcp://localhost:60504
        MuleClient client = new MuleClient();
        MuleManager.getConfiguration().setSynchronous(true);

        RemoteDispatcher dispatcher = client.getRemoteDispatcher(getServerUrl());
        UMOMessage message = dispatcher.receiveRemote(remoteEndpoint, 1000);
        assertNull(message);
        // We do a send instead of a dispatch here so the operation is
        // synchronous
        // thus eaiser to test
        dispatcher.sendRemote(remoteEndpoint, "Test Remote Message 2", null);

        message = dispatcher.receiveRemote(remoteEndpoint, 100000);
        assertNotNull(message);
        assertEquals("Test Remote Message 2", message.getPayload());

    }

    public String getServerUrl()
    {
        return "jms://jmsSysProvider/mule.sys.queue?createConnector=NEVER";
    }
}
