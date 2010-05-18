/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.transaction;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.tck.util.MuleDerbyTestUtils;
import org.mule.test.integration.transaction.extras.Book;
import org.mule.transport.jdbc.JdbcUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

public class XATransactionsWithSpringDAO extends FunctionalTestCase
{
    private static final int RECEIVE_TIMEOUT = 10000;
    private static String connectionString;

    protected String getConfigResources()
    {
        return "org/mule/test/integration/transaction/xatransactions-with-spring-dao-config.xml";
    }
    
    protected void suitePreSetUp() throws Exception
    {
        String dbName = MuleDerbyTestUtils.loadDatabaseName("derby.properties", "database.name");

        MuleDerbyTestUtils.defaultDerbyCleanAndInit("derby.properties", "database.name");
        connectionString = "jdbc:derby:" + dbName;

        super.suitePreSetUp();
    }

    protected void doSetUp() throws Exception
    {
        emptyTable();
    }

    protected void emptyTable() throws Exception
    {
        try
        {
            execSqlUpdate("DELETE FROM BOOK");
        }
        catch (Exception e)
        {
            execSqlUpdate("CREATE TABLE BOOK(ID INTEGER NOT NULL PRIMARY KEY,TITLE VARCHAR(255),AUTHOR VARCHAR(255))");
        }
    }

    protected Connection getConnection() throws Exception
    {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        return DriverManager.getConnection(connectionString);
    }

    public List execSqlQuery(String sql) throws Exception
    {
        Connection con = null;
        try
        {
            con = getConnection();
            return (List) new QueryRunner().query(con, sql, new ArrayListHandler());
        }
        finally
        {
            JdbcUtils.close(con);
        }
    }

    protected int execSqlUpdate(String sql) throws Exception
    {
        Connection con = null;
        try
        {
            con = getConnection();
            return new QueryRunner().update(con, sql);
        }
        finally
        {
            JdbcUtils.close(con);
        }
    }

    public void testXATransactionUsingSpringDaoNoRollback() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        Book book = new Book(1, "testBook", "testAuthor");
        client.sendNoReceive("jms://my.queue", book, null);
        MuleMessage result = client.request("vm://output", RECEIVE_TIMEOUT);
        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertTrue(((Boolean) result.getPayload()).booleanValue());
        int res = execSqlUpdate("UPDATE BOOK SET TITLE = 'My Test' WHERE TITLE='testBook'");
        if (res < 0)
        {
            fail();
        }
    }

    public void testXATransactionUsingSpringDaoWithRollback() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);

        Book book = new Book(1, "testBook", "testAuthor");
        client.sendNoReceive("jms://my.queue", book, null);
        MuleMessage result = client.request("vm://output", RECEIVE_TIMEOUT);
        assertNotNull(result);
        assertNotNull(result.getPayload());
        assertTrue(((Boolean) result.getPayload()).booleanValue());
        int res = execSqlUpdate("UPDATE BOOK SET TITLE = 'My Test' WHERE TITLE='testBook'");
        if (res < 0)
        {
            fail();
        }

        client.sendNoReceive("jms://my.queue", book, null);
        result = client.request("vm://output", 5000);
        // need to test that the Spring transaction has really been rolled back... 
        // from log file, it is
        assertNull(result);
    }
    
}
