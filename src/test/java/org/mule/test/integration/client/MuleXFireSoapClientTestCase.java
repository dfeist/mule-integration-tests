/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.client;


/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class MuleXFireSoapClientTestCase extends MuleAxisSoapClientTestCase
{
    public String getSoapProvider() {
        return "xfire";
    }

    // TODO fix: xfire doesn't currently support overloaded methods
    public void testRequestResponseComplex2() throws Exception {
        //no op
    }
}
