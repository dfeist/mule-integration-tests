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

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.expression.ExpressionEvaluator;
import org.mule.message.ExceptionMessage;
import org.mule.tck.junit4.FunctionalTestCase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ExceptionStrategyWithCustomExpressionEvaluatorTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/exceptions/exception-strategy-with-custom-expression-evaluator.xml";
    }

    @Test
    public void testCustomExpressionEvaluatorExceptionStrategy() throws MuleException
    {
        MuleClient client = muleContext.getClient();
        client.send("vm://in", TEST_MESSAGE, null);
        MuleMessage message = client.request("vm://out", DEFAULT_TEST_TIMEOUT_SECS);
        assertNotNull(message);
        assertTrue(message.getPayload() instanceof ExceptionMessage);
    }

    public static class FooExpressionEvaluator implements ExpressionEvaluator
    {

        public Object evaluate(String expression, MuleMessage message)
        {
            throw new UnsupportedOperationException("evaluate");
        }

        public void setName(String name)
        {
            throw new UnsupportedOperationException("setName");
        }

        public String getName()
        {
            return "Foo";
        }
    }

}
