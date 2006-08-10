/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.models;

import org.mule.impl.model.seda.SedaModel;
import org.mule.umo.model.UMOModel;

/**
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class SedaPipelineNoPoolingTestCase extends AbstractPipelineTestCase {
    protected String getModelType() {
        return "seda";
    }

    protected void configureModel(UMOModel model) {
        SedaModel m = (SedaModel)model;
        m.setEnablePooling(false);
    }
}
