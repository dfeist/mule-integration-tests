/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.providers.soap.axis;

import org.mule.config.ExceptionHelper;
import org.mule.config.builders.QuickConfigurationBuilder;
import org.mule.test.integration.providers.soap.AbstractSoapUrlEndpointFunctionalTestCase;
import org.mule.umo.UMOException;
import org.mule.umo.lifecycle.InitialisationException;


/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class AxisConnectorHttpFunctionalTestCase extends AbstractSoapUrlEndpointFunctionalTestCase
{

    static public class ComponentWithoutInterfaces
    {
        public String echo(String msg)
        {
            return msg;
        }
    }

    public String getConfigResources() {
        return "org/mule/test/integration/providers/soap/axis/axis-" + getTransportProtocol() + "-mule-config.xml";
    }

    protected String getTransportProtocol() {
        return "http";
    }

    protected String getSoapProvider() {
        return "axis";
    }

    /**
     * The Axis service requires that the component implements at least one interface
     * This just tests that we get the correct exception if no interfaces are implemented
     * @throws Throwable
     */
    public void testComponentWithoutInterfaces() throws Throwable
    {
        try {
            QuickConfigurationBuilder builder = new QuickConfigurationBuilder();
            builder.registerComponent(ComponentWithoutInterfaces.class.getName(),
                    "testComponentWithoutInterfaces", getComponentWithoutInterfacesEndpoint(), null, null);
            fail();
        } catch (UMOException e) {
            e = ExceptionHelper.getRootMuleException(e);
            assertTrue(e instanceof InitialisationException);
        }
    }
}
