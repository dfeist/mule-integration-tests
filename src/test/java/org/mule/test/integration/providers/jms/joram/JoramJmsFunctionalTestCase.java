/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.test.integration.providers.jms.joram;

import org.mule.providers.jms.JmsConnector;
import org.mule.test.integration.providers.jms.tools.JmsTestUtils;
import org.mule.umo.provider.UMOConnector;
import org.mule.test.integration.providers.jms.tools.JmsTestUtils;
import org.mule.test.integration.providers.jms.AbstractJmsFunctionalTestCase;

import javax.jms.Connection;
import java.util.Properties;

/**
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */

public class JoramJmsFunctionalTestCase extends AbstractJmsFunctionalTestCase
{
    public Connection getConnection() throws Exception
    {
        Properties p = JmsTestUtils.getJmsProperties(JmsTestUtils.JORAM_JMS_PROPERTIES);
        return JmsTestUtils.getQueueConnection(p);
    }

    public UMOConnector createConnector() throws Exception
    {
        JmsConnector connector = new JmsConnector();
        Properties props = JmsTestUtils.getJmsProperties(JmsTestUtils.JORAM_JMS_PROPERTIES);

        connector.setConnectionFactoryJndiName("JmsQueueConnectionFactory");
        connector.setProviderProperties(props);
        connector.setName(CONNECTOR_NAME);
        connector.getDispatcherThreadingProfile().setDoThreading(false);        

        return connector;
    }
}