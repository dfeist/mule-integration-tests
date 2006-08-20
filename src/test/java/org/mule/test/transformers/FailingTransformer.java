/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.transformers;

import org.mule.transformers.AbstractTransformer;
import org.mule.umo.transformer.TransformerException;

public class FailingTransformer extends AbstractTransformer {
    private static final long serialVersionUID = -4399792657994495343L;

    protected Object doTransform(Object src, String encoding) throws TransformerException {
        throw new TransformerException(this, new Exception("Wrapped test exception"));
    }
}
