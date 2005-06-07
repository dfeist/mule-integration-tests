/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.test.integration.providers.jdbc;

import javax.sql.DataSource;

import org.enhydra.jdbc.standard.StandardDataSource;
import org.mule.providers.jdbc.JdbcTransactionFactory;
import org.mule.umo.UMOTransactionFactory;

/**
 * @author Guillaume Nodet
 * @version $Revision$
 */
public class JdbcTransactionalJdbcFunctionalTestCase extends AbstractJdbcTransactionalFunctionalTestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected UMOTransactionFactory getTransactionFactory()
    {
        return new JdbcTransactionFactory();
    }

    protected DataSource createDataSource() throws Exception
    {
        StandardDataSource ds = new StandardDataSource();
        ds.setDriverName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:mem:.");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

}
