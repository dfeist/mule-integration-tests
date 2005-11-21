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
package org.mule.test.integration.models;

import org.mule.MuleManager;
import org.mule.components.simple.EchoComponent;
import org.mule.components.simple.StaticComponent;
import org.mule.config.builders.QuickConfigurationBuilder;
import org.mule.extras.client.MuleClient;
import org.mule.providers.vm.VMConnector;
import org.mule.tck.NamedTestCase;
import org.mule.umo.UMOMessage;
import org.mule.umo.model.UMOModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public abstract class AbstractPipelineTestCase extends NamedTestCase
{
    protected void setUp() throws Exception {
        if(MuleManager.isInstanciated()) MuleManager.getInstance().dispose();
    }

    protected void tearDown() throws Exception {
        MuleManager.getInstance().dispose();
    }

    protected abstract String getModelType();

    protected int getNumberOfMessages() {
        return 1000;
    }

    public void testPipelineSynchronous() throws Exception {

        QuickConfigurationBuilder builder = new QuickConfigurationBuilder(true);
        builder.createStartedManager(true, "", getModelType());
        configureModel(builder.getManager().getModel());
        builder.registerComponent(EchoComponent.class.getName(), "component1", "vm://component1", "vm://component2", null);
        builder.registerComponent(EchoComponent.class.getName(), "component2", "vm://component2", "vm://component3", null);
        Map props = new HashMap();
        props.put("data", "request received by component 3");
        builder.registerComponent(StaticComponent.class.getName(), "component3", "vm://component3", null, props);

        MuleClient client = new MuleClient();
        List results = new ArrayList();
        for (int i = 0; i < getNumberOfMessages(); i++) {
            UMOMessage result = client.send("vm://component1", "test", null);
            assertNotNull(result);
            results.add(result);
        }

        assertEquals(results.size(), getNumberOfMessages());
        for (Iterator iterator = results.iterator(); iterator.hasNext();) {
            UMOMessage umoMessage = (UMOMessage) iterator.next();
            assertEquals("request received by component 3", umoMessage.getPayloadAsString());
        }
    }

    public void testPipelineAsynchronous() throws Exception {
        QuickConfigurationBuilder builder = new QuickConfigurationBuilder(true);
        builder.createStartedManager(false, "", getModelType());

        VMConnector c = new VMConnector();
        c.setName("queuingConnector");
        c.setQueueEvents(true);
        builder.getManager().registerConnector(c);

        VMConnector c2 = new VMConnector();
        c2.setName("vmNoQueue");
        builder.getManager().registerConnector(c2);

        builder.registerComponent(EchoComponent.class.getName(), "component1", "vm://component1?connector=vmNoQueue", "vm://component2?connector=vmNoQueue", null);
        builder.registerComponent(EchoComponent.class.getName(), "component2", "vm://component2?connector=vmNoQueue", "vm://component3?connector=vmNoQueue", null);

        Map props = new HashMap();
        props.put("data", "request received by component 3");
        builder.registerComponent(StaticComponent.class.getName(), "component3", "vm://component3?connector=vmNoQueue", "vm://results?connector=queuingConnector", props);

        MuleClient client = new MuleClient();

        List results = new ArrayList();
        for (int i = 0; i < getNumberOfMessages(); i++) {
            client.dispatch("vm://component1", "test", null);
        }

        for (int i = 0; i < getNumberOfMessages(); i++) {
            UMOMessage result = client.receive("vm://results?connector=queuingConnector", 100000);
            assertNotNull(result);
            results.add(result);
        }
        assertEquals(results.size(), getNumberOfMessages());
        for (Iterator iterator = results.iterator(); iterator.hasNext();) {
            UMOMessage umoMessage = (UMOMessage) iterator.next();
            assertEquals("request received by component 3", umoMessage.getPayloadAsString());
        }
    }

    protected void configureModel(UMOModel model) {
        
    }
}
