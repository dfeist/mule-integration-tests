/* 
* $Header$
* $Revision$
* $Date$
* ------------------------------------------------------------------------------------------------------
* 
* Copyright (c) SymphonySoft Limited. All rights reserved.
* http://www.symphonysoft.com
* 
* The software in this package is published under the terms of the BSD
* style license a copy of which has been included with this distribution in
* the LICENSE.txt file. 
*
*/
package org.mule.test.usecases.axis;

/**
 * TODO document
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */
public class InvalidTradeException extends Exception {
    public InvalidTradeException() {
        super();
    }

    public InvalidTradeException(String message) {
        super(message);
    }

    public InvalidTradeException(Throwable cause) {
        super(cause);
    }

    public InvalidTradeException(String message, Throwable cause) {
        super(message, cause);
    }
}
