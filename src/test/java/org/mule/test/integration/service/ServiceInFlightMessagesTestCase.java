/*
 * $Id: EndpointBridgingTestCase.java 10662 2008-02-01 13:10:14Z romikk $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.service;

import org.mule.api.MuleException;
import org.mule.api.service.Service;
import org.mule.tck.FunctionalTestCase;
import org.mule.util.queue.FilePersistenceStrategy;
import org.mule.util.queue.QueueSession;
import org.mule.util.queue.TransactionalQueueManager;
import org.mule.util.xa.ResourceManagerSystemException;

public class ServiceInFlightMessagesTestCase extends FunctionalTestCase
{
    private static final int WAIT_TIME_MILLIS = 500;
    protected static final int NUM_MESSAGES = 500;

    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/service/service-inflight-messages.xml";
    }

    public void testInFlightMessages() throws Exception
    {
        Service service = muleContext.getRegistry().lookupService("TestService");
        populateSedaQueue(service, NUM_MESSAGES);

        muleContext.stop();

        assertNoLostMessages(NUM_MESSAGES, service);
        // Seda queue is empty because queue is not persistent and therefore is
        // emptied when service is stopped
        assertSedaQueueEmpty(service);
    }

    public void testInFlightMessagesPausedService() throws Exception
    {
        Service service = muleContext.getRegistry().lookupService("PausedTestService");
        populateSedaQueue(service, NUM_MESSAGES);

        muleContext.stop();

        // All message were lost so both queues are empty.
        assertSedaQueueEmpty(service);

        // TODO Enable the following assertion once MULE-4072 is fixed
        // assertOutboundVMQueueEmpty();
    }

    public void testInFlightStopPersistentMessages() throws Exception
    {
        Service service = muleContext.getRegistry().lookupService("TestPersistentQueueService");
        populateSedaQueue(service, NUM_MESSAGES);

        muleContext.stop();

        assertNoLostMessages(NUM_MESSAGES, service);
        // Persistent queue is being used so seda queue is not emptied when service
        // is stopped
        assertSedaQueueNotEmpty(service);

        // Start, process some messages, stop and make sure no messages get lost.
        muleContext.start();
        Thread.sleep(WAIT_TIME_MILLIS);
        muleContext.stop();

        assertNoLostMessages(NUM_MESSAGES, service);

        // Let mule finish up with the rest of the messages until seda queue is empty
        muleContext.start();
        Thread.sleep(WAIT_TIME_MILLIS * 8);
        muleContext.stop();

        assertNoLostMessages(NUM_MESSAGES, service);
        assertSedaQueueEmpty(service);
    }

    public void testInFlightStopPersistentMessagesPausedService() throws Exception
    {
        Service service = muleContext.getRegistry().lookupService("PausedTestPersistentQueueService");
        populateSedaQueue(service, NUM_MESSAGES);

        muleContext.stop();

        // Paused service does not process messages before or during stop().
        // TODO Enable the following assertion once MULE-4072 is fixed
        // assertOutboundVMQueueEmpty();
        assertNoLostMessages(NUM_MESSAGES, service);

        // Start, process some messages, stop and make sure no messages get lost.
        muleContext.start();
        service.resume();
        Thread.sleep(WAIT_TIME_MILLIS);
        muleContext.stop();

        assertNoLostMessages(NUM_MESSAGES, service);

        // Let mule finish up with the rest of the messages until seda queue is empty
        muleContext.start();
        Thread.sleep(WAIT_TIME_MILLIS * 14);
        muleContext.stop();

        assertNoLostMessages(NUM_MESSAGES, service);
        assertSedaQueueEmpty(service);
    }

    public void testInFlightDisposePersistentMessages() throws Exception
    {
        Service service = muleContext.getRegistry().lookupService("TestPersistentQueueService");
        populateSedaQueue(service, NUM_MESSAGES);

        muleContext.stop();
        assertNoLostMessages(NUM_MESSAGES, service);

        // Dispose and restart Mule and let it run for a short while
        muleContext.dispose();
        muleContext = createMuleContext();
        muleContext.start();
        Thread.sleep(WAIT_TIME_MILLIS);
        muleContext.stop();

        assertNoLostMessages(NUM_MESSAGES, service);

        // Let mule finish up with the rest of the messages until seda queue is empty
        muleContext.start();
        Thread.sleep(WAIT_TIME_MILLIS * 8);
        muleContext.stop();

        assertNoLostMessages(NUM_MESSAGES, service);
        assertSedaQueueEmpty(service);
    }

    protected void populateSedaQueue(Service service, int numMessages) throws MuleException, Exception
    {
        for (int i = 0; i < numMessages; i++)
        {
            service.dispatchEvent(getTestEvent("test", service, getTestInboundEndpoint("test://test")));
        }
    }

    /**
     * After each run the following should total 500 events: 1) Event still in SEDA
     * queue 2) Events dispatched to outbound vm endpooint 3) Events that were unable
     * to be sent to stopped service and raised exceptions
     */
    private synchronized void assertNoLostMessages(int numMessages, Service service)
        throws ResourceManagerSystemException
    {
        QueueSession queueSession = getTestQueueSession();
        
        int outQueueSize = queueSession.getQueue("out").size();
        
        String serviceName = service.getName() + ".service";
        int serviceQueueSize = queueSession.getQueue(serviceName).size();
        
        logger.warn("SEDA Queue: " + outQueueSize + ", Outbound endpoint vm queue: " + serviceQueueSize);
        assertEquals(numMessages, outQueueSize + serviceQueueSize);
    }

    protected synchronized void assertSedaQueueEmpty(Service service) throws ResourceManagerSystemException
    {
        QueueSession queueSession = getTestQueueSession();
        assertEquals(0, queueSession.getQueue(service.getName() + ".service").size());
    }

    protected synchronized void assertSedaQueueNotEmpty(Service service)
        throws ResourceManagerSystemException
    {
        QueueSession queueSession = getTestQueueSession();
        assertTrue(queueSession.getQueue(service.getName() + ".service").size() > 0);
    }

    protected synchronized void assertOutboundVMQueueEmpty() throws ResourceManagerSystemException
    {
        QueueSession queueSession = getTestQueueSession();
        assertEquals(0, queueSession.getQueue("out").size());
    }

    protected synchronized void assertOutboundVMQueueNotEmpty() throws ResourceManagerSystemException
    {
        QueueSession queueSession = getTestQueueSession();
        assertTrue(queueSession.getQueue("out").size() > 0);
    }

    protected QueueSession getTestQueueSession() throws ResourceManagerSystemException
    {
        TransactionalQueueManager tqm = new TransactionalQueueManager();
        FilePersistenceStrategy fps = new FilePersistenceStrategy();
        fps.setMuleContext(muleContext);
        tqm.setPersistenceStrategy(fps);
        tqm.start();
        QueueSession queueSession = tqm.getQueueSession();
        return queueSession;
    }
}
