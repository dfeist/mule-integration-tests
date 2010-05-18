/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.exceptions;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

public class ExceptionsWithRouterMule2715TestCase extends FunctionalTestCase
{

    public static final String MESSAGE = "message";
    public static final long TIMEOUT = 5000L;

    public void testWithRouter() throws Exception
    {
        doTest("with-router-in");
    }

    public void testWithoutRouter() throws Exception
    {
        doTest("without-router-in");
    }

    protected void doTest(String path) throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://" + path, MESSAGE, null);
        MuleMessage response = client.request("vm://error", TIMEOUT);
        assertNotNull("exception null", response.getExceptionPayload());
    }

    protected String getConfigResources()
    {
        return "org/mule/test/integration/exceptions/exceptions-with-router-mule-2715.xml";
    }

}
