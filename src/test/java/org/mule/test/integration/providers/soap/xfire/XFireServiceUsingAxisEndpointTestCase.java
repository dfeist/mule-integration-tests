/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.providers.soap.xfire;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mule.extras.client.MuleClient;
import org.mule.impl.MuleMessage;
import org.mule.tck.FunctionalTestCase;
import org.mule.umo.UMOMessage;

public class XFireServiceUsingAxisEndpointTestCase extends FunctionalTestCase{
	
	protected static transient Log logger = LogFactory.getLog(XFireServiceUsingAxisEndpointTestCase.class);
	
	public void testXfire() throws Exception
	{
		MuleClient client = new MuleClient();
		UMOMessage reply = client.send("vm://xfire.in", new MuleMessage("Testing String"));
		
		assertNotNull(reply);
		assertNotNull(reply.getPayload());
		assertEquals(reply.getPayloadAsString(),"Received: Testing String");
		logger.info(reply.getPayloadAsString());
	}
	
	public void testRequestWsdl() throws Exception
	{
		MuleClient client = new MuleClient();
		Map props = new HashMap();
		props.put("http.method", "GET");
		UMOMessage reply = client.send("http://localhost:82/services/XfireService?wsdl", "/services/Hello_Xfire?wsdl", props);
		
		assertNotNull(reply);
		assertNotNull(reply.getPayload());
		
		Document document = DocumentHelper.parseText(reply.getPayloadAsString());
		List nodes;
		
		nodes = document.selectNodes("//wsdl:definitions/wsdl:service");
		assertEquals(((Element)nodes.get(0)).attribute("name").getStringValue(),"XfireService");
	}

	protected String getConfigResources() {
		
		return "org/mule/test/integration/providers/soap/xfire/mule-config-xfire-using-axis.xml";
	}

}
