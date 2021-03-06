/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.core.routing.outbound;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.mule.functional.api.component.EventCallback;
import org.mule.functional.api.component.TestConnectorQueueHandler;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.test.AbstractIntegrationTestCase;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AggregationTimeoutTestCase extends AbstractIntegrationTestCase {

  private static final CountDownLatch blockExecution = new CountDownLatch(1);
  public static final String PROCESS_EVENT = "process";
  public static final String BLOCK_EVENT = "block";
  public static final String PROCESSED_EVENT = "processed";

  @Override
  protected String getConfigFile() {
    return "org/mule/test/integration/routing/outbound/aggregation-timeout-config.xml";
  }

  @Test
  public void timeoutsAggregationWithPersistentStore() throws Exception {
    List<String> inputData = new ArrayList<>();
    inputData.add(PROCESS_EVENT);
    inputData.add(BLOCK_EVENT);

    try {
      TestConnectorQueueHandler queueHandler = new TestConnectorQueueHandler(registry);

      // Need to return control to test case as soon as message is sent, and not wait for response.
      flowRunner("main").withPayload(inputData).dispatchAsync(muleContext.getSchedulerService()
          .ioScheduler(muleContext.getSchedulerBaseConfig().withShutdownTimeout(0, SECONDS)));

      Message response = queueHandler.read("testOut", RECEIVE_TIMEOUT).getMessage();
      assertThat(response.getPayload().getValue(), instanceOf(List.class));

      List<String> payloads = ((List<Message>) response.getPayload().getValue()).stream()
          .map(m -> (String) m.getPayload().getValue()).collect(toList());
      assertThat(payloads.size(), equalTo(1));
      assertThat(payloads, hasItem(PROCESSED_EVENT));
    } finally {
      // Release the blocked thread
      blockExecution.countDown();
    }
  }

  public static class BlockExecutionComponent implements EventCallback {

    @Override
    public void eventReceived(CoreEvent event, Object component, MuleContext muleContext) throws Exception {
      if (event.getMessage().getPayload().getValue().equals(BLOCK_EVENT)) {
        blockExecution.await();
      }
    }
  }
}
