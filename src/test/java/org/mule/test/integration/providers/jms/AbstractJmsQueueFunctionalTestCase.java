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

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

import java.util.List;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.QueueConnection;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicConnection;

import org.mule.MuleManager;
import org.mule.providers.jms.JmsMessageReceiver;
import org.mule.providers.jms.TransactedJmsMessageReceiver;
import org.mule.tck.functional.EventCallback;
import org.mule.test.integration.providers.jms.tools.JmsTestUtils;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOEventContext;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.provider.UMOConnector;
import org.mule.util.concurrent.CountDownLatch;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public abstract class AbstractJmsQueueFunctionalTestCase extends AbstractJmsFunctionalTestCase
{
    protected static CountDownLatch receiverIsUp;

    public void testSend() throws Exception
    {
        final CountDownLatch countDown = new CountDownLatch(2);
        receiverIsUp = new CountDownLatch(1);

        EventCallback callback = new EventCallback() {
            public void eventReceived(UMOEventContext context, Object Component)
            {
                callbackCalled = true;
                assertNull(context.getCurrentTransaction());
                countDown.countDown();
            }
        };

        initialiseComponent(callback);
        // Start the server
        MuleManager.getInstance().start();

        MessageConsumer mc;
        // check replyTo
        if (useTopics()) {
            mc = JmsTestUtils.getTopicSubscriber((TopicConnection) cnn, getOutDest().getAddress());
        } else {
            mc = JmsTestUtils.getQueueReceiver((QueueConnection) cnn, getOutDest().getAddress());
        }
        mc.setMessageListener(new MessageListener() {
            public void onMessage(Message message)
            {
                currentMsg = message;
                countDown.countDown();
            }
        });

        logger.debug("Waiting for coutdown isReceiverUp");
        assertTrue(receiverIsUp.tryLock(LOCK_WAIT, TimeUnit.MILLISECONDS));
        receiverIsUp = null;

        send(DEFAULT_MESSAGE, false, Session.AUTO_ACKNOWLEDGE, null);
        assertTrue(countDown.tryLock(LOCK_WAIT, TimeUnit.MILLISECONDS));

        assertNotNull(currentMsg);
        assertTrue(currentMsg instanceof TextMessage);
        assertEquals(DEFAULT_MESSAGE + " Received", ((TextMessage) currentMsg).getText());

        assertTrue(callbackCalled);
    }

    public void testSendWithReplyTo() throws Exception
    {
        final CountDownLatch countDown = new CountDownLatch(2);
        receiverIsUp = new CountDownLatch(1);

        EventCallback callback = new EventCallback() {
            public void eventReceived(UMOEventContext context, Object Component)
            {
                callbackCalled = true;
                assertNull(context.getCurrentTransaction());
                countDown.countDown();
            }
        };

        initialiseComponent(callback);
        // Start the server
        MuleManager.getInstance().start();

        MessageConsumer mc;
        // check replyTo
        if (useTopics()) {
            mc = JmsTestUtils.getTopicSubscriber((TopicConnection) cnn, "replyto");
        } else {
            mc = JmsTestUtils.getQueueReceiver((QueueConnection) cnn, "replyto");
        }
        mc.setMessageListener(new MessageListener() {
            public void onMessage(Message message)
            {
                currentMsg = message;
                countDown.countDown();
            }
        });

        logger.debug("Waiting for coutdown isReceiverUp");
        assertTrue(receiverIsUp.tryLock(LOCK_WAIT, TimeUnit.MILLISECONDS));
        receiverIsUp = null;

        send(DEFAULT_MESSAGE, false, Session.AUTO_ACKNOWLEDGE, "replyto");

        assertTrue(countDown.tryLock(LOCK_WAIT, TimeUnit.MILLISECONDS));

        assertNotNull(currentMsg);
        assertTrue(currentMsg instanceof TextMessage);
        assertEquals(DEFAULT_MESSAGE + " Received", ((TextMessage) currentMsg).getText());
        assertTrue(callbackCalled);
    }

    public boolean useTopics()
    {
        return false;
    }

    protected static class JmsMessageReceiverSynchronous extends TransactedJmsMessageReceiver
    {
        public JmsMessageReceiverSynchronous(UMOConnector connector, UMOComponent component, UMOEndpoint endpoint)
                throws InitialisationException
        {
            super(connector, component, endpoint);
        }

        protected List getMessages() throws Exception
        {
            if (receiverIsUp != null) {
                logger.debug("Releasing coutdown isReceiverUp");
                receiverIsUp.countDown();
            }
            return super.getMessages();
        }
    }
}
