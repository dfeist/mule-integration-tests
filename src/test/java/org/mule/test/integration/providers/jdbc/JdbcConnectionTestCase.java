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

import org.hsqldb.jdbc.jdbcDataSource;
import org.mule.MuleManager;
import org.mule.impl.MuleDescriptor;
import org.mule.impl.endpoint.MuleEndpoint;
import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.providers.SimpleRetryConnectionStrategy;
import org.mule.providers.jdbc.JdbcConnector;
import org.mule.tck.testmodels.fruit.Orange;
import org.mule.umo.UMOComponent;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.provider.UMOConnector;

import javax.sql.DataSource;

import java.util.HashMap;

/**
 * 
 * This test must be run manually.
 * See the comments inline in testReconnection
 * 
 * @author <a href="mailto:gnt@codehaus.org">Guillaume Nodet</a>
 *
 */
public class JdbcConnectionTestCase extends AbstractJdbcFunctionalTestCase {

	protected JdbcConnector connector;
	
    protected void emptyTable() throws Exception
    {
        // TODO this overrides super.emptyTable() - is this correct?
        // the entire test seems to be incomplete, see the comments below..
    }
	
    public UMOConnector createConnector() throws Exception
    {
    	connector = (JdbcConnector) super.createConnector();
        SimpleRetryConnectionStrategy strategy = new SimpleRetryConnectionStrategy();
        strategy.setRetryCount(10);
        strategy.setFrequency(1000);
        strategy.setDoThreading(true);
        connector.setConnectionStrategy(strategy);
        return connector;
    }

	protected DataSource createDataSource() throws Exception {
        jdbcDataSource ds = new jdbcDataSource();
        ds.setDatabase("jdbc:hsqldb:hsql://localhost");
        ds.setUser("sa");
		return ds;
	}
    
	public void testReconnection() throws Exception {

        MuleDescriptor d = getTestDescriptor("anOrange", Orange.class.getName());

        UMOComponent component = MuleManager.getInstance().getModel().registerComponent(d);
        UMOEndpoint endpoint = new MuleEndpoint("test",
                                                new MuleEndpointURI("jdbc://test?sql=SELECT * FROM TABLE"),
                                                connector,
                                                null,
                                                UMOEndpoint.ENDPOINT_TYPE_SENDER,
                                                0, null,
                                                new HashMap());
        MuleManager.getInstance().start();
        connector.registerListener(component, endpoint);

		// The hsqldb instance should be put offline before starting test
        // The receiver should try to connect to the database
        //
        // Then put hsqldb online.
        // Check that the receiver reconnect and polls the database
        //
        // Put hsqldb offline.
        // The receiver should try to connect to the database. 
        Thread.sleep(1000);
	}

}
