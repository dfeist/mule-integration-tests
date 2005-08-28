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
*
*/
package org.mule.test.integration.providers.http;

import org.mule.MuleManager;
import org.mule.config.ConfigurationBuilder;
import org.mule.config.builders.MuleXmlConfigurationBuilder;
import org.mule.extras.client.MuleClient;
import org.mule.tck.NamedTestCase;
import org.mule.umo.UMOException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class TwoEndpointsSinglePortTestCase extends NamedTestCase
 {
    protected void setUp() throws Exception
    {
        super.setUp();
        if (MuleManager.isInstanciated())
            MuleManager.getInstance().dispose();
        ConfigurationBuilder configBuilder = new MuleXmlConfigurationBuilder();
        configBuilder.configure("org/mule/test/integration/providers/http/two-endpoints-single-port.xml");
    }

    public void testSendToEach() throws Exception {

        sendWithResponse("http://localhost:8081/mycomponent1", "test", "mycomponent1", 10);
        sendWithResponse("http://localhost:8081/mycomponent2", "test", "mycomponent2", 10);
    }

    protected void sendWithResponse(String endpoint, String message, String response, int noOfMessages) throws UMOException {
        MuleClient client = new MuleClient();

        List results = new ArrayList();
        for (int i = 0; i < noOfMessages; i++) {
            results.add(client.send(endpoint, message, null).getPayload());
        }

        assertEquals(noOfMessages, results.size());
        for (int i = 0; i < noOfMessages; i++) {
            assertEquals(response, results.get(i).toString());
        }
    }
}
