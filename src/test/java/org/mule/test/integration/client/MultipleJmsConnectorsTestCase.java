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

import org.mule.extras.client.MuleClient;
import org.mule.tck.NamedTestCase;

import java.util.Properties;

/**
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */
public class MultipleJmsConnectorsTestCase extends NamedTestCase
{
    public void testMultipleJmsClientConnections() throws Exception
    {
        MuleClient client = new MuleClient();
        client.setProperty("jms.connectionFactoryJndiName", "ConnectionFactory");
        client.setProperty("jms.jndiInitialFactory", "org.activemq.jndi.ActiveMQInitialContextFactory");
        client.setProperty("jms.specification", "1.1");
        Properties props = new Properties();
        props.put("brokerURL", "tcp://localhost:61616");
        client.setProperty("jms.jndiProviderProperties", props);

        client.dispatch("jms://admin:admin@admin.queue?createConnector=ALWAYS", "testing", null);
        client.dispatch("jms://ross:ross@ross.queue?createConnector=ALWAYS", "testing", null);

        assertEquals(2, client.getManager().getConnectors().size());
    }
}
