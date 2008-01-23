/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.transport.jdbc;

import org.mule.api.transaction.TransactionFactory;
import org.mule.transport.jdbc.JdbcTransactionFactory;

public class JdbcTransactionalJdbcFunctionalTestCase extends AbstractJdbcTransactionalFunctionalTestCase
{

    protected TransactionFactory getTransactionFactory()
    {
        return new JdbcTransactionFactory();
    }

}
