/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.test.integration.service;

import javax.jms.TextMessage;

/**
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */
public class TestComponent
{
    private int count=0;

    public String receive(String message) throws Exception
    {
       System.out.println("Received: " + message + " Number: " + inc() + " in thread: " + Thread.currentThread().getName());
       return "Received: " + message;
    }

    public String receive(TextMessage message) throws Exception
    {
       System.out.println("Received: " + message.getText() + " Number: " + inc() + " in thread: " + Thread.currentThread().getName());
        return "Received: " + message.getText();
    }

    protected int inc()
    {
        count++;
        return count;
    }
}
