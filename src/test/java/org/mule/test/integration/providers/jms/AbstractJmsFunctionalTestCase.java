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
package org.mule.test.integration.providers.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.MuleManager;
import org.mule.config.PoolingProfile;
import org.mule.config.builders.QuickConfigurationBuilder;
import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.impl.model.seda.SedaModel;
import org.mule.providers.jms.JmsConnector;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.functional.EventCallback;
import org.mule.tck.functional.FunctionalTestComponent;
import org.mule.test.integration.providers.jms.tools.JmsTestUtils;
import org.mule.umo.endpoint.MalformedEndpointException;
import org.mule.umo.endpoint.UMOEndpointURI;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueConnection;
import javax.jms.TopicConnection;

import java.util.HashMap;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public abstract class AbstractJmsFunctionalTestCase extends AbstractMuleTestCase
{
    public static final String DEFAULT_IN_QUEUE = "jms://in.q";
    public static final String DEFAULT_OUT_QUEUE = "jms://out.q";
    public static final String DEFAULT_DL_QUEUE = "jms://dlq";
    public static final String DEFAULT_IN_TOPIC = "jms://topic:in.t";
    public static final String DEFAULT_OUT_TOPIC = "jms://topic:out.t";
    public static final String DEFAULT_DL_TOPIC = "jms://topic:dlt";
    public static final String DEFAULT_MESSAGE = "Test Message";
    public static final String CONNECTOR_NAME = "testConnector";

    public static final long LOCK_WAIT = 20000;

    protected JmsConnector connector;
    protected boolean callbackCalled = false;
    protected Connection cnn;
    protected Message currentMsg;
    protected int eventCount = 0;

    protected final transient Log logger = LogFactory.getLog(getClass());

    protected void doSetUp() throws Exception
    {
        // By default the JmsTestUtils use the openjms config, though you can
        // pass in other configs using the property below

        // Make sure we are running synchronously
        MuleManager.getConfiguration().setSynchronous(true);
        MuleManager.getConfiguration()
                   .getPoolingProfile()
                   .setInitialisationPolicy(PoolingProfile.POOL_INITIALISE_ONE_COMPONENT);

        MuleManager.getInstance().setModel(new SedaModel());
        callbackCalled = false;
        ConnectionFactory cf = getConnectionFactory();

        cnn = getSenderConnection();
        cnn.start();
        //drainDestinations();
        connector = createConnector();
        connector.setConnectionFactory(cf);
        MuleManager.getInstance().registerConnector(connector);
        currentMsg = null;
        eventCount = 0;
    }

    protected Connection getSenderConnection() throws Exception {
        return getConnectionFactory().createConnection();
    }

    protected void doTearDown() throws Exception
    {
        try {
            if(cnn!=null) {
                cnn.close();
            }
        } catch (JMSException e) {
            // TODO shouldn't this be caught?
        }
    }

//    protected void drainDestinations() throws Exception
//    {
//        if (!useTopics()) {
//            System.out.println("@@@@ Draining Queues @@@@");
//            JmsTestUtils.drainQueue((QueueConnection) cnn, getInDest().getAddress());
//            assertNull(receive(getInDest().getAddress(), 10));
//            JmsTestUtils.drainQueue((QueueConnection) cnn, getOutDest().getAddress());
//            assertNull(receive(getOutDest().getAddress(), 10));
//        }
//
//    }

    public abstract ConnectionFactory getConnectionFactory() throws Exception;

    public void initialiseComponent(EventCallback callback) throws Exception
    {
        QuickConfigurationBuilder builder = new QuickConfigurationBuilder();
        HashMap props = new HashMap();
        props.put("eventCallback", callback);

        builder.registerComponent(FunctionalTestComponent.class.getName(),
                                  "testComponent",
                                  getInDest(),
                                  getOutDest(),
                                  props);
    }

    public void afterInitialise() throws Exception
    {
        // template method
    }

    protected UMOEndpointURI getInDest()
    {
        try {
            if (!useTopics()) {
                return new MuleEndpointURI(DEFAULT_IN_QUEUE);
            } else {
                return new MuleEndpointURI(DEFAULT_IN_TOPIC);
            }
        } catch (MalformedEndpointException e) {
            fail(e.getMessage());
            return null;
        }
    }

    protected UMOEndpointURI getOutDest()
    {
        try {
            if (!useTopics()) {
                return new MuleEndpointURI(DEFAULT_OUT_QUEUE);
            } else {
                return new MuleEndpointURI(DEFAULT_OUT_TOPIC);
            }
        } catch (Exception e) {
            fail(e.getMessage());
            return null;
        }
    }

    protected void send(String payload, boolean transacted, int ack, String replyTo) throws JMSException
    {
        if (!useTopics()) {
            JmsTestUtils.queueSend((QueueConnection) cnn, getInDest().getAddress(), payload, transacted, ack, replyTo);
        } else {
            JmsTestUtils.topicPublish((TopicConnection) cnn,
                                      getInDest().getAddress(),
                                      payload,
                                      transacted,
                                      ack,
                                      replyTo);
        }
    }

    protected Message receive(String dest, long timeout) throws JMSException
    {
        Message msg = null;
        if (!useTopics()) {
            msg = JmsTestUtils.queueReceiver((QueueConnection) cnn, dest, timeout);
        } else {
            msg = JmsTestUtils.topicSubscribe((TopicConnection) cnn, dest, timeout);
        }
        return msg;
    }

    public boolean useTopics()
    {
        return false;
    }

    public abstract JmsConnector createConnector() throws Exception;
}
