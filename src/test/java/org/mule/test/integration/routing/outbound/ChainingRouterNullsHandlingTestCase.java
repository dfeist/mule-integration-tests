/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.routing.outbound;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.util.ExceptionUtils;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class ChainingRouterNullsHandlingTestCase extends AbstractServiceAndFlowTestCase
{

    public ChainingRouterNullsHandlingTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{{ConfigVariant.SERVICE,
            "org/mule/test/integration/routing/outbound/chaining-router-null-handling-service.xml"}});
    }

    @Test
    public void testNoComponentFails() throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);
        MuleMessage message = new DefaultMuleMessage("thePayload", muleContext);
        MuleMessage result = muleClient.send("vm://incomingPass", message);
        assertNull("Shouldn't have any exceptions", result.getExceptionPayload());
        assertEquals("thePayload Received component1 Received component2Pass", result.getPayloadAsString());
    }

    @Test
    public void testLastComponentFails() throws Exception
    {
        MuleMessage message = new DefaultMuleMessage("thePayload", muleContext);
        try
        {
            muleContext.getClient().send("vm://incomingLastFail", message);
            fail("Exception expected");
        }
        catch (Exception e)
        {
            assertTrue("Exception raised in wrong service",
                ExceptionUtils.getRootCause(e) instanceof Component2Exception);
        }
    }

    @Test
    public void testFirstComponentFails() throws Exception
    {
        MuleMessage message = new DefaultMuleMessage("thePayload", muleContext);
        try
        {
            muleContext.getClient().send("vm://incomingFirstFail", message);
            fail("Exception expected");
        }
        catch (Exception e)
        {
            assertTrue("Exception raised in wrong service",
                ExceptionUtils.getRootCause(e) instanceof Component1Exception);
        }
    }
}
