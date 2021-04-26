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

package io.github.alttpj.emu2api.source.retroarch

import static org.awaitility.Awaitility.await

import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.time.Duration
import java.util.concurrent.CompletableFuture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RetroArchConnectionTest {

  DatagramChannel channel
  Selector selector
  ReplyToVersionAction responseAction

  @BeforeEach
  void 'open channel'() {
    this.channel = DatagramChannel.open()
    channel.socket().bind(new InetSocketAddress("localhost", 55755))
    channel.configureBlocking(false)
    this.selector = Selector.open()
    channel.register(selector, SelectionKey.OP_READ)

    this.responseAction = new ReplyToVersionAction()
    CompletableFuture.runAsync(() -> selector.select(responseAction, 5000L))
  }

  @AfterEach
  void 'close channel'() {
    this.selector.close()
    this.channel.close()
  }

  @Test
  void 'test connection failed'() {
    given:
    def device = new RetroArchDevice("test", "localhost", 55655, "udp");
    def connection = new RetroArchConnection(device)

    expect:
    assert !connection.canConnect()
  }

  @Test
  void 'test connection successful on version number response'() {
    given:
    def device = new RetroArchDevice("test", "localhost", 55755, "udp");
    def connection = new RetroArchConnection(device)

    expect:
    assert connection.canConnect()
    await()
        .pollDelay(Duration.ofMillis(50L))
        .atMost(Duration.ofMillis(100L))
        .until(responseAction.responded::get)
    assert responseAction.responded.get()
  }
}
