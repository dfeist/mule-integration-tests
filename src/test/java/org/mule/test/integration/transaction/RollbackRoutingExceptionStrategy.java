/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.transaction;

import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.routing.RoutingException;
import org.mule.exception.AbstractMessagingExceptionStrategy;

/**
 * Will rollback the transaction in case a {@link RoutingException} is encountered.
 */
public class RollbackRoutingExceptionStrategy extends AbstractMessagingExceptionStrategy
{

    public void handleRoutingException(MuleMessage message, MessageProcessor endpoint, Throwable t)
    {
        logger.debug("handleRoutingException: endpoint=" + endpoint + " message=" + message);
        defaultHandler(t);
        handleTransaction(t);
        routeException(message, endpoint, t);
    }

}
