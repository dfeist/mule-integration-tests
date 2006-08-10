/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.service;

/**
 * <code>PeopleService</code> is a test service that returns complex types
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public interface PeopleService
{
    public Person getPerson(String firstName);

    public Person[] getPeople();

    public void addPerson(Person person) throws Exception;

    public Person addPerson(String firstname, String surname) throws Exception;
}
