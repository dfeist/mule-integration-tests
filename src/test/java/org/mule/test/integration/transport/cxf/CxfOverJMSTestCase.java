/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.transport.cxf;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;
import org.mule.module.client.MuleClient;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.tck.FunctionalTestCase;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class CxfOverJMSTestCase extends AbstractServiceAndFlowTestCase
{
    private static final String req = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                                      + "<soap:Body>"
                                      + "<ns2:echo xmlns:ns2=\"http://simple.component.mule.org/\">"
                                      + "<ns2:echo>hello</ns2:echo>"
                                      + "</ns2:echo>"
                                      + "</soap:Body>"
                                      + "</soap:Envelope>";

    public CxfOverJMSTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }
    
    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "org/mule/test/integration/transport/cxf/cxf-over-jms-config-service.xml"},
            {ConfigVariant.FLOW, "org/mule/test/integration/transport/cxf/cxf-over-jms-config-flow.xml"}
        });
    }        

    @Test
    public void testCxf() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("jms://TestComponent", new DefaultMuleMessage(req, muleContext));
        MuleMessage message = client.request("jms://testout", 10000);
        assertNotNull(message.getPayload());
        assertTrue(message.getPayloadAsString().indexOf("return>hello") != -1);
    }

    @Test
    public void testCxfClientOverJMS() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        DefaultMuleMessage msg = new DefaultMuleMessage("hello", muleContext);
        msg.setProperty("method", "echo", PropertyScope.INVOCATION);
        client.dispatch("cxf:jms://TestComponent2", msg);
        MuleMessage message = client.request("jms://testout", 10000);
        assertNotNull("message reply is null", message);
        assertNotNull("message payload is null", message.getPayload());
        assertTrue(message.getPayloadAsString().equals("hello"));
    }

    // MULE-4677
    public void XXtestCxfOverJMSSyncProxy() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        MuleMessage result = client.send("http://localhost:63081/services/testBridge",
            new DefaultMuleMessage(req, muleContext));
        assertNotNull(result.getPayload());
        assertTrue(result.getPayloadAsString().contains("<ns2:echo>hello</ns2:echo>"));
    }
}
