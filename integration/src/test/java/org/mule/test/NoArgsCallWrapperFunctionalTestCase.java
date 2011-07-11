/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.AbstractServiceAndFlowTestCase;

/**
 * This test has been re-written to use entry point resolvers.
 */
public class NoArgsCallWrapperFunctionalTestCase extends AbstractServiceAndFlowTestCase
{
    private static final int RECEIVE_TIMEOUT = 5000;

    public NoArgsCallWrapperFunctionalTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "no-args-call-wrapper-config-service.xml"},
            {ConfigVariant.FLOW, "no-args-call-wrapper-config-flow.xml"}});
    }

    @Test
    public void testNoArgsCallWrapper() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://invoke", "test", null);
        MuleMessage reply = client.request("vm://out", RECEIVE_TIMEOUT);
        assertNotNull(reply);
        assertNull(reply.getExceptionPayload());
        assertEquals("Just an apple.", reply.getPayload());
    }

    @Test
    public void testWithInjectedDelegate() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://invokeWithInjected", "test", null);
        MuleMessage reply = client.request("vm://outWithInjected", RECEIVE_TIMEOUT);
        assertNotNull(reply);
        assertNull(reply.getExceptionPayload());
        // same as original input
        assertEquals("test", reply.getPayload());
    }

}
