/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.components;

import org.mule.api.MuleMessage;
import org.mule.extras.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

/**
 * This test re-written to use entry point resolvers.  As a consequence, some tests, which verified
 * implementation details rather than functionality, were dropped.
 */
public class NoArgsCallComponentTestCase extends FunctionalTestCase
{

    public static final String INPUT_DC_QUEUE_NAME = "vm://in";
    public static final String OUTPUT_DC_QUEUE_NAME = "vm://out";
    public static final String INPUT_DI_QUEUE_NAME = "vm://invokeWithInjected";
    public static final String OUTPUT_DI_QUEUE_NAME = "vm://outWithInjected";

    public static final String DEFAULT_INPUT_MESSAGE = "test";
    public static final String DEFUALT_OUTPUT_MESSAGE = "Just an apple.";

    public static final String SERVICE_DC_NAME="WORMS";
    public static final String SERVICE_DI_NAME="TIRANA";

    public static final String DELEGATE_DC_METHOD_NAME="toString";
    public static final String DELEGATE_DI_METHOD_NAME="wash";

    public static final int TIMEOUT = 5000;

    protected String getConfigResources()
    {
        return "org/mule/test/integration/components/no-args-call-component-functional-test.xml";
    }

    public void testDelegateClass() throws Exception
    {
        MuleClient client = new MuleClient();
        client.dispatch(INPUT_DC_QUEUE_NAME, "test", null);
        MuleMessage message = client.request(OUTPUT_DC_QUEUE_NAME, TIMEOUT);
        assertNotNull(message);
        assertEquals(message.getPayload(), DEFUALT_OUTPUT_MESSAGE);
        client.dispose();

    }

    public void testWithInjectedDelegate() throws Exception
    {
        MuleClient client = new MuleClient();
        client.dispatch(INPUT_DI_QUEUE_NAME, DEFAULT_INPUT_MESSAGE, null);
        MuleMessage reply = client.request(OUTPUT_DI_QUEUE_NAME, TIMEOUT);
        assertNotNull(reply);
        assertNull(reply.getExceptionPayload());
        // same as original input
        assertEquals(DEFAULT_INPUT_MESSAGE, reply.getPayload());
    }



}
