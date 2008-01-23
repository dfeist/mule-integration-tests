/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.config;

import org.mule.tck.FunctionalTestCase;

/**
 * Tests Deploying and referencing components from two different spring container
 * contexts
 * 
 * TODO MULE-1789
 */
public class MultiContainerTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "org/mule/test/integration/config/multi-container-test.xml";
    }

    public void testSanity()
    {
        fail("TODO MULE-1789");
    }
    
    public void testContainer() throws Exception
    {
//        Registry registry = muleContext.getRegistry();
//        assertNotNull(registry);
//        assertNotNull(registry.lookupObject("spring2-Apple"));
//        assertNotNull(registry.lookupObject("spring-Apple"));
//        assertNotNull(registry.lookupObject("spring2-Banana"));
//        assertNotNull(registry.lookupObject("spring-Banana"));
//
//        assertNull(registry.lookupObject("WaterMelon"));
    }

//    public void testSpecificContainerAddressing() throws Exception
//    {
//        Registry registry = muleContext.getRegistry();
//        assertNotNull(registry);
//        Orange o = (Orange)registry.lookupObject(new ContainerKeyPair("spring1", "Orange"));
//        assertNotNull(o);
//        assertEquals(new Integer(8), o.getSegments());
//
//        o = (Orange)registry.lookupObject(new ContainerKeyPair("spring2", "Orange"));
//        assertNotNull(o);
//        assertEquals(new Integer(10), o.getSegments());
//
//        // gets the component from the first container
//        o = (Orange)registry.lookupObject("Orange");
//        assertNotNull(o);
//        assertEquals(new Integer(8), o.getSegments());
//    }
//
//    public void testSpecificContainerAddressingForComponents() throws Exception
//    {
//        QuickConfigurationBuilder builder = new QuickConfigurationBuilder();
//        UMODescriptor d = builder.createDescriptor("Orange", "myOrange", "test://foo", null, null);
//        //d.setContainer("spring2");
//        builder.registerComponent(d);
//        Component c = builder.getMuleContext().getRegistry().lookupModel("main").getComponent("myOrange");
//        assertNotNull(c);
//        Object o = c.getInstance();
//        assertTrue(o instanceof Orange);
//        Orange orange = (Orange)o;
//        assertEquals(10, orange.getSegments().intValue());
//
//        d = builder.createDescriptor("Orange", "myOrange2", "test://bar", null, null);
//        //d.setContainer("spring1");
//        builder.registerComponent(d);
//        c = builder.getMuleContext().getRegistry().lookupModel("main").getComponent("myOrange2");
//        assertNotNull(c);
//        o = c.getInstance();
//        assertTrue(o instanceof Orange);
//        orange = (Orange)o;
//        assertEquals(8, orange.getSegments().intValue());
//
//    }
}
