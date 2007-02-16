/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.providers.jms.activemq;

import org.mule.impl.RequestContext;
import org.mule.providers.jms.transformers.AbstractJmsTransformer;
import org.mule.providers.jms.transformers.JMSMessageToObject;
import org.mule.providers.jms.transformers.ObjectToJMSMessage;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.util.compression.CompressionStrategy;
import org.mule.util.compression.GZipCompression;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * <code>JmsTransformersTestCase</code> Tests the JMS transformer implementations.
 */

public class JmsTransformersTestCase extends AbstractMuleTestCase
{
    private static ActiveMQConnectionFactory factory = null;
    private static Session session = null;

    protected void suitePreSetUp() throws Exception
    {
        factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false");

        session = factory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    protected void doTearDown() throws Exception
    {
        RequestContext.setEvent(null);
    }

    protected void suitePostTearDown() throws Exception
    {
        session.close();
        session = null;
        factory = null;
    }

    public void testTransObjectMessage() throws Exception
    {
        RequestContext.setEvent(getTestEvent("test"));

        ObjectMessage oMsg = session.createObjectMessage();
        File f = new File("/some/random/path");
        oMsg.setObject(f);
        AbstractJmsTransformer trans = new JMSMessageToObject();
        Object result = trans.transform(oMsg);
        assertTrue("Transformed object should be a File", result.getClass().equals(File.class));

        AbstractJmsTransformer trans2 = new SessionEnabledObjectToJMSMessage(session);
        trans2.setReturnClass(ObjectMessage.class);
        Object result2 = trans2.transform(f);
        assertTrue("Transformed object should be an object message", result2 instanceof ObjectMessage);
    }

    public void testTransTextMessage() throws Exception
    {
        RequestContext.setEvent(getTestEvent("test"));

        String text = "This is a test TextMessage";
        TextMessage tMsg = session.createTextMessage();
        tMsg.setText(text);

        AbstractJmsTransformer trans = new JMSMessageToObject();
        Object result = trans.transform(tMsg);
        assertTrue("Transformed object should be a string", text.equals(result.toString()));

        AbstractJmsTransformer trans2 = new SessionEnabledObjectToJMSMessage(session);
        trans2.setReturnClass(TextMessage.class);
        Object result2 = trans2.transform(text);
        assertTrue("Transformed object should be a TextMessage", result2 instanceof TextMessage);
    }

    public void testTransMapMessage() throws Exception
    {
        RequestContext.setEvent(getTestEvent("test"));

        Properties p = new Properties();
        p.setProperty("Key1", "Value1");
        p.setProperty("Key2", "Value2");
        p.setProperty("Key3", "Value3");

        AbstractJmsTransformer trans = new SessionEnabledObjectToJMSMessage(session);
        trans.setReturnClass(MapMessage.class);
        Object result2 = trans.transform(p);
        assertTrue("Transformed object should be a MapMessage", result2 instanceof MapMessage);

        MapMessage mMsg = (MapMessage)result2;
        AbstractJmsTransformer trans2 = new JMSMessageToObject();
        trans2.setReturnClass(Map.class);
        Object result = trans2.transform(mMsg);
        assertTrue("Transformed object should be a Map", result instanceof Map);
    }

    public void testTransByteMessage() throws Exception
    {
        RequestContext.setEvent(getTestEvent("test"));

        AbstractJmsTransformer trans = new SessionEnabledObjectToJMSMessage(session);
        trans.setReturnClass(BytesMessage.class);
        String text = "This is a test BytesMessage";
        Object result2 = trans.transform(text.getBytes());
        assertTrue("Transformed object should be a BytesMessage", result2 instanceof BytesMessage);

        AbstractJmsTransformer trans2 = new JMSMessageToObject();
        trans2.setReturnClass(byte[].class);
        BytesMessage bMsg = (BytesMessage)result2;
        Object result = trans2.transform(bMsg);
        assertTrue("Transformed object should be a byte[]", result instanceof byte[]);
        String res = new String((byte[])result);
        assertEquals("Source and result should be equal", text, res);
    }

    // The following test is disabled because - belive it or not - ActiveMQ 3.2.4
    // unconditionally uncompresses BytesMessages for reading, even if it is not
    // supposed to do so (the layer doing the message reading seems to have no access
    // to the Broker configuration and seems to assume that compressed data was
    // compressed by ActiveMQ for more efficient wire transport). This may or may not
    // be fixed in 4.x.
    // For more information why this is VERY BAD read:
    // http://en.wikipedia.org/wiki/Zip_of_death
    public void testCompressedBytesMessage() throws Exception
    {
        RequestContext.setEvent(getTestEvent("test"));

        // use GZIP
        CompressionStrategy compressor = new GZipCompression();

        // create compressible data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < 5000; i++)
        {
            baos.write(i);
        }

        byte[] originalBytes = baos.toByteArray();
        byte[] compressedBytes = compressor.compressByteArray(originalBytes);
        assertTrue("Source compressedBytes should be compressed", compressor.isCompressed(compressedBytes));

        // now create a BytesMessage from the compressed byte[]
        AbstractJmsTransformer trans = new SessionEnabledObjectToJMSMessage(session);
        trans.setReturnClass(BytesMessage.class);
        Object result2 = trans.transform(compressedBytes);
        assertTrue("Transformed object should be a Bytes message", result2 instanceof BytesMessage);

        // check whether the BytesMessage contains the compressed bytes
        BytesMessage intermediate = (BytesMessage)result2;
        intermediate.reset();
        byte[] intermediateBytes = new byte[(int)(intermediate.getBodyLength())];
        int intermediateSize = intermediate.readBytes(intermediateBytes);
        assertTrue("Intermediate bytes must be compressed", compressor.isCompressed(intermediateBytes));
        assertTrue("Intermediate bytes must be equal to compressed source", Arrays.equals(compressedBytes,
            intermediateBytes));
        assertEquals("Intermediate bytes and compressed source must have same size", compressedBytes.length,
            intermediateSize);

        // now test the other way around: getting the byte[] from a manually created
        // BytesMessage
        AbstractJmsTransformer trans2 = new JMSMessageToObject();
        trans2.setReturnClass(byte[].class);
        BytesMessage bMsg = session.createBytesMessage();
        bMsg.writeBytes(compressedBytes);
        Object result = trans2.transform(bMsg);
        assertTrue("Transformed object should be a byte[]", result instanceof byte[]);
        assertTrue("Result should be compressed", compressor.isCompressed((byte[])result));
        assertTrue("Source and result should be equal", Arrays.equals(compressedBytes, (byte[])result));
    }

    /*
     * This class overrides getSession() to return the specified test Session;
     * otherwise we would need a full-fledged JMS connector with dispatchers etc.
     */
    public static class SessionEnabledObjectToJMSMessage extends ObjectToJMSMessage
    {
        private static final long serialVersionUID = -440672187466417761L;
        private final Session transformerSession;

        public SessionEnabledObjectToJMSMessage(Session session)
        {
            super();
            transformerSession = session;
        }

        // @Override
        protected Session getSession()
        {
            return transformerSession;
        }
    }

}
