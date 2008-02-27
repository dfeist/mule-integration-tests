/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.xml.functional;

import org.mule.api.MuleException;

import java.io.IOException;

public class RoundRobinXmlSplitterFunctionalTestCase extends AbstractXmlOutboundFunctionalTestCase
{

    public void testSimple() throws MuleException, IOException
    {
        doSend("roundrobin-det");
        assertServices(ROUND_ROBIN_ENDPOINT_PREFIX, 1,  new String[]{SERVICE_SPLITTER, ROUND_ROBIN_INDET});
        assertService(ROUND_ROBIN_ENDPOINT_PREFIX, 2, ROUND_ROBIN_DET);
    }

    public void testDeterministic() throws MuleException, IOException
    {
        doSend("roundrobin-det");
        assertServices(ROUND_ROBIN_ENDPOINT_PREFIX, 1,  new String[]{SERVICE_SPLITTER, ROUND_ROBIN_INDET});
        assertService(ROUND_ROBIN_ENDPOINT_PREFIX, 2, ROUND_ROBIN_DET);
        doSend("roundrobin-det");
        assertServices(ROUND_ROBIN_ENDPOINT_PREFIX, 1, new String[]{SERVICE_SPLITTER, ROUND_ROBIN_INDET});
        assertService(ROUND_ROBIN_ENDPOINT_PREFIX, 2, ROUND_ROBIN_DET);
        doSend("roundrobin-det");
        doSend("roundrobin-det");
        assertServices(ROUND_ROBIN_ENDPOINT_PREFIX, 1, new String[]{SERVICE_SPLITTER, ROUND_ROBIN_INDET, SERVICE_SPLITTER, ROUND_ROBIN_INDET});
        assertServices(ROUND_ROBIN_ENDPOINT_PREFIX, 2, new String[]{ROUND_ROBIN_DET, ROUND_ROBIN_DET});
    }

    public void testIndeterministic() throws MuleException, IOException
    {
        doSend("roundrobin-indet");
        assertServices(ROUND_ROBIN_ENDPOINT_PREFIX, 1,  new String[]{SERVICE_SPLITTER, ROUND_ROBIN_INDET});
        assertService(ROUND_ROBIN_ENDPOINT_PREFIX, 2, ROUND_ROBIN_DET);
        doSend("roundrobin-indet");
        assertServices(ROUND_ROBIN_ENDPOINT_PREFIX, 2,  new String[]{SERVICE_SPLITTER, ROUND_ROBIN_INDET});
        assertService(ROUND_ROBIN_ENDPOINT_PREFIX, 1, ROUND_ROBIN_DET);
    }

}
