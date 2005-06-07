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

import java.util.Properties;

import javax.jms.Connection;

import org.mule.providers.jms.JmsConnector;
import org.mule.providers.jms.JmsTransactionFactory;
import org.mule.test.integration.providers.jms.AbstractJmsTransactionFunctionalTest;
import org.mule.test.integration.providers.jms.tools.JmsTestUtils;
import org.mule.umo.UMOTransactionFactory;
import org.mule.umo.provider.UMOConnector;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @author Guillaume Nodet
 * @version $Revision$
 */

public class ActiveMQJmsTransactionFunctionalTestCase extends AbstractJmsTransactionFunctionalTest
{
    public UMOTransactionFactory getTransactionFactory()
    {
        return new JmsTransactionFactory();
    }

    public Connection getConnection() throws Exception
    {
        // default to ActiveMq for Jms 1.1 support
        Properties p = JmsTestUtils.getJmsProperties(JmsTestUtils.ACTIVE_MQ_JMS_PROPERTIES);
        Connection cnn = JmsTestUtils.getQueueConnection(p);
        cnn.start();
        return cnn;
    }

    public UMOConnector createConnector() throws Exception
    {
        JmsConnector connector = new JmsConnector();
        connector.setSpecification(JmsConnector.JMS_SPECIFICATION_11);
        Properties props = JmsTestUtils.getJmsProperties(JmsTestUtils.ACTIVE_MQ_JMS_PROPERTIES);

        connector.setConnectionFactoryJndiName("JmsQueueConnectionFactory");
        connector.setProviderProperties(props);
        connector.setName(CONNECTOR_NAME);
        connector.getDispatcherThreadingProfile().setDoThreading(false);
        connector.getReceiverThreadingProfile().setMaxThreadsActive(1);

        return connector;
    }

}
