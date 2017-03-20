package org.mule.test.construct;

/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

import java.util.List;

import org.mule.runtime.core.api.transformer.TransformerException;
import org.mule.runtime.core.transformer.simple.StringAppendTransformer;

/**
 * transformer for tests involving transformation from lists to string concatenated
 *
 */

public class TestStringAppendTransformer extends StringAppendTransformer {

  public TestStringAppendTransformer(String append) {
    super(append);
  }

  /**
   * Tranforms a list of string to its concatenation
   * 
   * @param args arguments to concatenate
   * @return arguments transformed as concatenation of string
   * @throws TransformerException
   */
  public Object transformArray(List<String> args) throws TransformerException {
    StringBuffer buffer = new StringBuffer();

    for (String arg : args) {
      buffer.append(arg);
    }

    return transform(buffer.toString());
  }

}
