/*
 * $Header: $
 * $Revision: $
 * $Date: $
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.test.integration.persistence;

import org.mule.MuleManager;
import org.mule.extras.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

import java.io.File;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 */
public class FilePersistenceTestCase extends FunctionalTestCase {

    protected String getConfigResources() {
        return "org/mule/test/integration/persistence/file-persistence-config.xml";
    }

    public void testFilesStored() throws Exception {

        //Note that the FunctionalTestCase will remove the working directory after each execution
        String path = MuleManager.getConfiguration().getWorkingDirectory() + "/queuestore/test.queue";
        File store = new File(path);
        assertFalse(store.exists());

        MuleClient client = new MuleClient();
        client.send("vm://test.queue", "test", null);
        File[] files = store.listFiles();
        assertNotNull(files);
        assertEquals(1, files.length);

        MuleManager.getInstance().getModel().startComponent("TestComponent");
        //give the component some time to initialise
        Thread.sleep(2000);
        files = store.listFiles();
        assertNotNull(files);
        assertEquals(0, files.length);

    }
}
