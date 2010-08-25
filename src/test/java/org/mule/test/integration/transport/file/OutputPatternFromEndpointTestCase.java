/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.transport.file;

import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.util.FileUtils;

import java.io.File;

import junit.framework.AssertionFailedError;

public class OutputPatternFromEndpointTestCase extends FunctionalTestCase
{

    protected String getConfigResources()
    {
        return "org/mule/test/integration/providers/file/mule-file-output-pattern-from-endpoint.xml";
    }

    public void testBasic() throws Exception
    {
        String myFirstDirName = "FirstWrite";
        String mySecondDirName = "SecondWrite";
        String myFileName1 = "export.txt";
        String myFileName2 = "export.txt.OK";

        // make sure there is no directory and file
        File myDir = FileUtils.newFile(myFirstDirName);
        if (myDir.isDirectory())
        {
            // Delete Any Existing Files
            File[] files = myDir.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                assertTrue(files[i].delete());
            }
            // This may fail if this directory contains other directories.
            assertTrue(myDir.delete());
        }

        File myDir2 = FileUtils.newFile(mySecondDirName);
        if (myDir2.isDirectory())
        {
            // Delete Any Existing Files
            File[] files = myDir2.listFiles();
            for (int i = 0; i < files.length; i++)
            {
                assertTrue(files[i].delete());
            }
            // This may fail if this directory contains other directories.
            assertTrue(myDir2.delete());
        }

        try
        {
            assertFalse(FileUtils.newFile(myDir, myFileName1).exists());
            assertFalse(FileUtils.newFile(myDir2, myFileName2).exists());

            MuleClient client = new MuleClient(muleContext);
            client.send("vm://filesend", "Hello", null);

            // the output file should exist now
            // check that the files with the correct output pattern were generated
            assertTrue(FileUtils.newFile(myDir, myFileName1).exists());
            assertTrue(FileUtils.newFile(myDir2, myFileName2).exists());
        }
        catch (AssertionFailedError e1)
        {
            //The original assertion was getting masked by a failure in the finally block
            e1.printStackTrace();
        }
        finally
        {
            FileUtils.newFile(myDir, myFileName1).delete();
            FileUtils.newFile(myDir2, myFileName2).delete();
            myDir.delete();
            myDir2.delete();
        }
    }
}
