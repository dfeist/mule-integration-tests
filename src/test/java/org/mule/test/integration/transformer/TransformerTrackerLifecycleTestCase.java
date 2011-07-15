/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.transformer;

import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.tck.AbstractServiceAndFlowTestCase;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;

public class TransformerTrackerLifecycleTestCase extends AbstractServiceAndFlowTestCase
{
    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "org/mule/test/transformers/transformer-lifecycle-service.xml"},
            {ConfigVariant.FLOW, "org/mule/test/transformers/transformer-lifecycle-flow.xml"}});
    }

    public TransformerTrackerLifecycleTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Test
    public void testLifecycle() throws Exception
    {

        MuleClient muleClient = muleContext.getClient();

        final MuleMessage result = muleClient.send("vm://EchoService.In", "foo", null);

        final LifecycleTrackerTransformer ltt = (LifecycleTrackerTransformer) result.getPayload();

        muleContext.dispose();

        // TODO MULE-5002 initialise called twice
        assertEquals("[setProperty, setMuleContext, initialise, setMuleContext, initialise, start, stop]",
            ltt.getTracker().toString());
    }
}
