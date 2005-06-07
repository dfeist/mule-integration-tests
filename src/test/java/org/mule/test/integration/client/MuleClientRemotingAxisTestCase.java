/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.test.integration.client;

import org.mule.MuleManager;
import org.mule.config.ConfigurationBuilder;
import org.mule.config.MuleProperties;
import org.mule.config.builders.MuleXmlConfigurationBuilder;
import org.mule.extras.client.MuleClient;
import org.mule.extras.client.RemoteDispatcher;
import org.mule.tck.NamedTestCase;
import org.mule.test.integration.service.Person;
import org.mule.umo.UMOMessage;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class MuleClientRemotingAxisTestCase extends NamedTestCase
{
    protected void setUp() throws Exception
    {
        super.setUp();
        System.setProperty(MuleProperties.DISABLE_SERVER_CONNECTIONS, "false");
        if (MuleManager.isInstanciated()) {
            MuleManager.getInstance().dispose();
        }
        ConfigurationBuilder configBuilder = new MuleXmlConfigurationBuilder();
        configBuilder.configure("org/mule/test/integration/client/axis-test-mule-config.xml");
        System.setProperty(MuleProperties.DISABLE_SERVER_CONNECTIONS, "true");
        // Be sure the manager is fully started
        Thread.sleep(1000);
    }

    public void testRequestResponse() throws Throwable
    {
        MuleClient client = new MuleClient();
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("tcp://localhost:38000");
        try {
            UMOMessage result = dispatcher.sendRemote("axis:http://localhost:38004/mule/services/mycomponent2?method=echo", "test", null);
            assertNotNull(result);
            assertEquals("test", result.getPayloadAsString());
        } finally {
            client.dispose();
        }
    }

    public void testRequestResponseComplex() throws Exception
    {
        MuleClient client = new MuleClient();
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("tcp://localhost:38000");
        try {
            UMOMessage result = dispatcher.sendRemote("axis:http://localhost:38004/mule/services/mycomponent3?method=getPerson", "Fred", null);
            assertNotNull(result);
            System.out.println(result.getPayload());
            assertTrue(result.getPayload() instanceof Person);
            assertEquals("Fred", ((Person) result.getPayload()).getFirstName());
            assertEquals("Flintstone", ((Person) result.getPayload()).getLastName());
        } finally {
            client.dispose();
        }
    }

    public void testRequestResponseComplex2() throws Exception
    {
        MuleClient client = new MuleClient();
        RemoteDispatcher dispatcher = client.getRemoteDispatcher("tcp://localhost:38000");
        try {
            String[] args = new String[] { "Ross", "Mason" };
            UMOMessage result = dispatcher.sendRemote("axis:http://localhost:38004/mule/services/mycomponent3?method=addPerson", args, null);
            assertNotNull(result);
            assertTrue(result.getPayload() instanceof Person);
            assertEquals("Ross", ((Person) result.getPayload()).getFirstName());
            assertEquals("Mason", ((Person) result.getPayload()).getLastName());
    
            // do a receive
            result = client.send("axis:http://localhost:38004/mule/services/mycomponent3?method=getPerson", "Ross", null);
            assertNotNull(result);
            assertTrue(result.getPayload() instanceof Person);
            assertEquals("Ross", ((Person) result.getPayload()).getFirstName());
            assertEquals("Mason", ((Person) result.getPayload()).getLastName());
        } finally {
            client.dispose();
        }
    }
}
