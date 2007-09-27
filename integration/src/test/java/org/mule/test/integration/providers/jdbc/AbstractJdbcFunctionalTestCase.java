/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.providers.jdbc;

import org.mule.RegistryContext;
import org.mule.config.PoolingProfile;
import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.impl.model.seda.SedaModel;
import org.mule.providers.jdbc.JdbcConnector;
import org.mule.providers.jdbc.JdbcUtils;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.functional.FunctionalTestComponent;
import org.mule.umo.UMOEventContext;
import org.mule.umo.endpoint.EndpointException;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.umo.model.UMOModel;
import org.mule.umo.provider.UMOConnector;
import org.mule.util.MuleDerbyTestUtils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.enhydra.jdbc.standard.StandardDataSource;

public abstract class AbstractJdbcFunctionalTestCase extends AbstractMuleTestCase
{

    public static final String DEFAULT_IN_URI = "jdbc://getTest?type=1";
    public static final String DEFAULT_OUT_URI = "jdbc://writeTest?type=2";
    public static final String CONNECTOR_NAME = "testConnector";
    public static final String DEFAULT_MESSAGE = "Test Message";

    public static final String SQL_READ = "SELECT ID, TYPE, DATA, ACK, RESULT FROM TEST WHERE TYPE = ${type} AND ACK IS NULL";
    public static final String SQL_ACK = "UPDATE TEST SET ACK = ${NOW} WHERE ID = ${id} AND TYPE = ${type} AND DATA = ${data}";
    public static final String SQL_WRITE = "INSERT INTO TEST(TYPE, DATA, ACK, RESULT) VALUES(${type}, ${payload}, NULL, NULL)";
    
    public static String EMBEDDED_CONNECTION_STRING;
    public static final String EMBEDDED_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
    
    public static String CLIENT_CONNECTION_STRING ;
    public static final String CLIENT_DRIVER_NAME = "org.apache.derby.jdbc.ClientDriver";

    protected UMOConnector connector;
    protected UMOModel model;
    protected DataSource dataSource;
    
    private static boolean derbySetupDone = false;
    
    protected void suitePreSetUp() throws Exception
    {
        if (!derbySetupDone)
        {
            String dbName = MuleDerbyTestUtils.loadDatabaseName("derby.properties", "database.name");
            MuleDerbyTestUtils.defaultDerbyCleanAndInit("derby.properties", "database.name");
            EMBEDDED_CONNECTION_STRING = "jdbc:derby:" + dbName;
            CLIENT_CONNECTION_STRING = "jdbc:derby://localhost:1527/"+ dbName +";create=true";
            derbySetupDone = true;
        }
        super.suitePreSetUp();
    }

    protected void doSetUp() throws Exception
    {
        // Make sure we are running synchronously
        RegistryContext.getConfiguration().setDefaultSynchronousEndpoints(true);
        SedaModel model = new SedaModel();
        model.setName("main");
        model.getPoolingProfile().setInitialisationPolicy(
            PoolingProfile.INITIALISE_ONE);
        managementContext.getRegistry().registerModel(model);
        // Create and register connector
        connector = createConnector();
        managementContext.getRegistry().registerConnector(connector);
        // Empty table
        emptyTable();
    }

    protected void emptyTable() throws Exception
    {
        try
        {
            int updated = execSqlUpdate("DELETE FROM TEST");
            int x = updated;
        }
        catch (Exception e)
        {
            execSqlUpdate("CREATE TABLE TEST(ID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 0)  NOT NULL PRIMARY KEY,TYPE INTEGER,DATA VARCHAR(255),ACK TIMESTAMP,RESULT VARCHAR(255))");
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

    protected Object[] execSqlQuery(String sql) throws Exception
    {
        Connection con = null;
        try
        {
            con = getConnection();
            return (Object[])new QueryRunner().query(con, sql, new ArrayHandler());
        }
        finally
        {
            JdbcUtils.close(con);
        }
    }

    public static class JdbcFunctionalTestComponent extends FunctionalTestComponent
    {
        public Object onCall(UMOEventContext context) throws Exception
        {
            if (getEventCallback() != null)
            {
                getEventCallback().eventReceived(context, this);
            }
            Map map = (Map)context.getMessage().getPayload();
            return map.get("data") + " Received";
        }
    }

    public Connection getConnection() throws Exception
    {
        Object dataSource = getDataSource();
        if (dataSource instanceof DataSource)
        {
            return ((DataSource)dataSource).getConnection();
        }
        else
        {
            return ((XADataSource)dataSource).getXAConnection().getConnection();
        }
    }

    public DataSource getDataSource() throws Exception
    {
        if (dataSource == null)
        {
            dataSource = createDataSource();
        }
        return dataSource;
    }

    public UMOConnector createConnector() throws Exception
    {
        JdbcConnector connector = new JdbcConnector();
        connector.setDataSource(getDataSource());
        connector.setName(CONNECTOR_NAME);
        connector.getDispatcherThreadingProfile().setDoThreading(false);
        connector.setPollingFrequency(5000);

        Map queries = new HashMap();
        queries.put("getTest", SQL_READ);
        queries.put("getTest.ack", SQL_ACK);
        queries.put("writeTest", SQL_WRITE);
        connector.setQueries(queries);

        return connector;
    }

    protected UMOEndpointURI getInDest()
    {
        try
        {
            return new MuleEndpointURI(DEFAULT_IN_URI);
        }
        catch (EndpointException e)
        {
            fail(e.getMessage());
            return null;
        }
    }

    protected UMOEndpointURI getOutDest()
    {
        try
        {
            return new MuleEndpointURI(DEFAULT_OUT_URI);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
            return null;
        }
    }
    
    //by default use the embedded datasource
    protected DataSource createDataSource() throws Exception
    {
        return createEmbeddedDataSource();
    }
    
    protected DataSource createEmbeddedDataSource() throws Exception
    {
        StandardDataSource ds = new StandardDataSource();
        ds.setDriverName(EMBEDDED_DRIVER_NAME);
        ds.setUrl(EMBEDDED_CONNECTION_STRING);
        return ds;
    }
    
    protected DataSource createClientDataSource() throws Exception
    {
        StandardDataSource ds = new StandardDataSource();
        ds.setDriverName(CLIENT_DRIVER_NAME);
        ds.setUrl(CLIENT_CONNECTION_STRING);
        return ds;
    }

}
