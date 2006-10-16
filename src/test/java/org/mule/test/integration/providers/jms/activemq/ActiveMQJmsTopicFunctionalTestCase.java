/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.providers.jms.activemq;

public class ActiveMQJmsTopicFunctionalTestCase extends ActiveMQJmsQueueFunctionalTestCase
{

    public boolean useTopics()
    {
        return true;
    }

}
