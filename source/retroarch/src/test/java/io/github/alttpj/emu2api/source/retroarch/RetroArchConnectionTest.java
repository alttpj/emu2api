/*
 * Copyright 2021-2021 the ALttPJ Team @ https://github.com/alttpj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alttpj.emu2api.source.retroarch;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RetroArchConnectionTest {

  @BeforeEach
  public void openChannel() throws IOException {
    this.channel = DatagramChannel.open();
    this.channel.socket().bind(new InetSocketAddress("localhost", 55755));
    this.channel.configureBlocking(false);
    this.selector = Selector.open();
    this.channel.register(this.selector, SelectionKey.OP_READ);

    this.responseAction = new ReplyToVersionAction();
    CompletableFuture.runAsync(() -> {
      try {
        this.selector.select(this.responseAction, 500L);
      } catch (final IOException javaIoIOException) {
        throw new UncheckedIOException(javaIoIOException);
      }
    });
  }

  @AfterEach
  public void closeChannel() throws IOException {
    this.selector.close();
    this.channel.close();
  }

  @Test
  @DisplayName("test connection failed")
  public void testConectionFailed() {
    // given:
    final RetroArchDevice device = new RetroArchDevice("test", "localhost", 55655, "udp");
    final RetroArchConnection connection = new RetroArchConnection(device);

    // expect:
    assertFalse(connection.canConnect());
  }

  @Test
  @DisplayName("test connection successful on version number response")
  public void testConnectionSuccessfulOnVersionNumberResponse() {
    // given:
    final RetroArchDevice device = new RetroArchDevice("test", "localhost", 55755, "udp");
    final RetroArchConnection connection = new RetroArchConnection(device);

    // expect:
    assertTrue(connection.canConnect());
    await()
        .pollDelay(Duration.ofMillis(50L))
        .atMost(Duration.ofMillis(100L))
        .until(this.getResponseAction()::getResponded);
    assertTrue(this.responseAction.getResponded());
  }

  public DatagramChannel getChannel() {
    return this.channel;
  }

  public void setChannel(final DatagramChannel channel) {
    this.channel = channel;
  }

  public Selector getSelector() {
    return this.selector;
  }

  public void setSelector(final Selector selector) {
    this.selector = selector;
  }

  public ReplyToVersionAction getResponseAction() {
    return this.responseAction;
  }

  public void setResponseAction(final ReplyToVersionAction responseAction) {
    this.responseAction = responseAction;
  }

  private DatagramChannel channel;
  private Selector selector;
  private ReplyToVersionAction responseAction;
}
