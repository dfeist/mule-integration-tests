/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.messaging.meps;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.NullPayload;

// START SNIPPET: full-class
public class InOptionalOutOutOnlyAsyncRouterTestCase extends FunctionalTestCase
{
    public static final long TIMEOUT = 3000;

    protected String getConfigResources()
    {
        return "org/mule/test/integration/messaging/meps/pattern_In-Optional-Out_Out-Only-Async-Router.xml";
    }

    public void testExchange() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);

        MuleMessage result = client.send("inboundEndpoint", "some data", null);
        assertNotNull(result);
        assertEquals(NullPayload.getInstance(), result.getPayload());
        assertNull(result.getExceptionPayload());

        DefaultMuleMessage msg = new DefaultMuleMessage("some data", muleContext);
        msg.setOutboundProperty("foo", "bar");
        result = client.send("inboundEndpoint", msg);
        assertNotNull(result);
        assertEquals("got it!", result.getPayloadAsString());
    }
}
// END SNIPPET: full-class
