/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

/*
 * Copyright 2002-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or =mplied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mule.test.integration.providers.soap.axis.style;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.soap.SOAPEnvelope;

/**
 * Simple message-style service sample.
 */

public interface MessageService
{
    /**
     * Service methods, echo back any XML received.
     */

    public org.apache.axis.message.SOAPBodyElement[] soapBodyElement(org.apache.axis.message.SOAPBodyElement[] bodyElements);

    public Document document(Document body);

    public Element[] elementArray(Element[] elems);

    public void soapRequestResponse(SOAPEnvelope req, SOAPEnvelope resp) throws javax.xml.soap.SOAPException;

}
