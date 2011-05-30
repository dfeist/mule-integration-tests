/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.exceptions;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.routing.filter.Filter;
import org.mule.api.transaction.RollbackMethod;
import org.mule.construct.SimpleFlowConstruct;
import org.mule.tck.FunctionalTestCase;
import org.mule.util.concurrent.Latch;

import java.util.concurrent.TimeUnit;

public class ExceptionStrategyFilterTestCase extends FunctionalTestCase
{
    private Latch exceptionHandlerLatch = new Latch();

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/exceptions/exception-strategy-filter.xml";
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();
        installCustomExceptionHandler();
    }

    private void installCustomExceptionHandler() throws Exception
    {
        FlowConstruct flow = getFlowConstruct("filter");
        assertNotNull(flow);

        SimpleFlowConstruct simpleFlow = (SimpleFlowConstruct) flow;
        simpleFlow.setExceptionListener(new TestMessagingExceptionHandler(exceptionHandlerLatch));
    }

    public void testExceptionThrownFromMessageFilterIsHandledByExceptionHandler() throws Exception
    {
        muleContext.getClient().send("vm://in", TEST_MESSAGE, null);

        assertTrue("Exception thrown by MessageFilter was not handled by the flow's MessagingExceptionHandler",
            exceptionHandlerLatch.await(RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));
    }

    public static class FalseFilter implements Filter
    {
        @Override
        public boolean accept(MuleMessage message)
        {
            return false;
        }
    }

    private static class TestMessagingExceptionHandler implements MessagingExceptionHandler
    {
        private Latch latch;

        public TestMessagingExceptionHandler(Latch latch)
        {
            super();
            this.latch = latch;
        }

        @Override
        public MuleEvent handleException(Exception exception, MuleEvent event, RollbackMethod method)
        {
            latch.release();
            return event;
        }

        @Override
        public MuleEvent handleException(Exception exception, MuleEvent event)
        {
            latch.release();
            return null;
        }
    }
}
