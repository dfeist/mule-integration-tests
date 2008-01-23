/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.routing.replyto;

import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.extras.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

import java.util.HashMap;
import java.util.Map;

public class ReplytoChainIntegration2TestCase extends FunctionalTestCase
{
    public ReplytoChainIntegration2TestCase()
    {
        setDisposeManagerPerSuite(true);
    }

    protected String getConfigResources()
    {
        return "org/mule/test/integration/routing/replyto/replyto-chain-integration-test-2.xml";
    }

    public void testReplyToChain() throws Exception
    {
        String message = "test";

        MuleClient client = new MuleClient();
        Map props = new HashMap();
        props.put(MuleProperties.MULE_REMOTE_SYNC_PROPERTY, "false");
        MuleMessage result = client.send("vm://pojo1", message, props);
        assertNotNull(result);
        assertEquals("Received: " + message, result.getPayload());
    }
}
