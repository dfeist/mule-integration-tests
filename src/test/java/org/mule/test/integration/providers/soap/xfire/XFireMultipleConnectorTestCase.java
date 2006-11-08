/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.providers.soap.xfire;

import org.mule.extras.client.MuleClient;
import org.mule.impl.MuleMessage;
import org.mule.tck.FunctionalTestCase;
import org.mule.umo.UMOMessage;

public class XFireMultipleConnectorTestCase extends FunctionalTestCase
{

    public XFireMultipleConnectorTestCase()
    {
        super();
        this.setDisposeManagerPerSuite(true);
    }
    
    protected String getConfigResources()
    {
        return "org/mule/test/integration/providers/soap/xfire/xfire_multiple_connector.xml";
    }
    
    public void testXFireConnector1() throws Exception
    {
        MuleClient client = new MuleClient();
        
        UMOMessage reply = client.send("xfire:http://localhost:8081/services/TestComponent1?method=receive", new MuleMessage("mule"));
        
        assertEquals("Received: mule", reply.getPayloadAsString());
    }
    
    public void testXFireConnector2() throws Exception
    {
        MuleClient client = new MuleClient();
        
        UMOMessage reply = client.send("xfire:http://localhost:8082/services/TestComponent2?method=receive", new MuleMessage("mule"));
        
        assertEquals("Received: mule", reply.getPayloadAsString());
    }

}


