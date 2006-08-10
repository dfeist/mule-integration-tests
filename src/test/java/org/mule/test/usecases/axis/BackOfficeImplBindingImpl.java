/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

public class BackOfficeImplBindingImpl implements org.mule.test.usecases.axis.BackOfficeImpl
 {
    //Doc Lit test
    public SubmitTradeResponse submitTrade(SubmitTrade parameters) {
        TradeStatus ts = new TradeStatus();
        Trade trade = parameters.getArg0();
        ts.setTradeID(trade.getTradeID());
        ts.setStatus("RECEIVED");
        SubmitTradeResponse str = new SubmitTradeResponse(ts);
        return str;
    }

    //RPC Enc test
    public TradeStatus submitTrade(Trade trade) {
        TradeStatus ts = new TradeStatus();
        ts.setTradeID(trade.getTradeID());
        ts.setStatus("RECEIVED");
        return ts;
    }

    //Wrapped Lit test
    public TradeStatus submitTrade(int accountID, String cusip, int currency,
                                   int tradeID, int transaction) {
        Trade trade = new Trade();
        trade.setAccountID(accountID);
        trade.setCusip(cusip);
        trade.setCurrency(currency);
        trade.setTradeID(tradeID);
        trade.setTransaction(transaction);

        TradeStatus ts = new TradeStatus();
        ts.setTradeID(trade.getTradeID());
        ts.setStatus("RECEIVED");
        return ts;
    }

}
