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

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.NullPayload;

public abstract class AbstractXmlPropertyExtractorTestCase extends FunctionalTestCase
{
    public static long WAIT_PERIOD = 5000L;

    private boolean matchSingle = true;

    protected AbstractXmlPropertyExtractorTestCase(boolean matchSingle)
    {
        this.matchSingle = matchSingle;
    }

    protected abstract Object getMatchMessage() throws Exception;

    protected abstract Object getErrorMessage() throws Exception;

    protected String getConfigResources()
    {
        return "org/mule/module/xml/property-extractor-test.xml";
    }

    public void testMatch() throws Exception
    {
        MuleClient client = new MuleClient();
        client.dispatch("in", getMatchMessage(), null);
        MuleMessage message = client.request("vm://match1?connector=queue", WAIT_PERIOD);
        assertNotNull(message);
        assertFalse(message.getPayload() instanceof NullPayload);
        if(!matchSingle)
        {
            message = client.request("vm://match2?connector=queue", WAIT_PERIOD);
            assertNotNull(message);
            assertFalse(message.getPayload() instanceof NullPayload);
        }
    }
    

    public void testError() throws Exception
    {
        MuleClient client = new MuleClient();
        client.dispatch("in", getErrorMessage(), null);
        MuleMessage message = client.request("vm://error?connector=queue", WAIT_PERIOD);
        assertNotNull(message);
        assertFalse(message.getPayload() instanceof NullPayload);
    }
    
}
