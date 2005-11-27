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
package org.mule.test.integration.providers.jms.activemq;

import org.activemq.ActiveMQConnectionFactory;
import org.mule.providers.jms.JmsConnector;
import org.mule.providers.jms.JmsConstants;
import org.mule.test.integration.providers.jms.AbstractJmsQueueFunctionalTestCase;
import org.mule.umo.provider.UMOConnector;

import javax.jms.Connection;
import java.util.HashMap;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public class ActiveMQJmsQueueFunctionalTestCase extends AbstractJmsQueueFunctionalTestCase
{
    protected ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();

    public Connection getConnection() throws Exception
    {
        factory.setUseEmbeddedBroker(true);
        factory.setBrokerURL("vm://localhost");
        return factory.createQueueConnection();
    }

    public UMOConnector createConnector() throws Exception
    {
        JmsConnector connector = new JmsConnector();
        connector.setSpecification(JmsConstants.JMS_SPECIFICATION_11);
        connector.setConnectionFactory(factory);
        connector.setName(CONNECTOR_NAME);
        connector.getDispatcherThreadingProfile().setDoThreading(false);

        HashMap overrides = new HashMap();
        overrides.put("message.receiver", JmsMessageReceiverSynchronous.class.getName());
        connector.setServiceOverrides(overrides);
        return connector;
    }
}
