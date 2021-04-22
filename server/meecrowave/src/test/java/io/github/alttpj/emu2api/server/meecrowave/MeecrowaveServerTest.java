/*
 * Copyright 2021-${year} the ALttPJ Team @ https://github.com/alttpj
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

package io.github.alttpj.emu2api.server.meecrowave;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alttpj.emu2api.endpoint.ws.data.Usb2SnesRequest;
import io.github.alttpj.emu2api.endpoint.ws.json.JsonbProvider;
import jakarta.json.bind.Jsonb;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.net.URI;
import java.time.Duration;
import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.junit5.MeecrowaveConfig;
import org.apache.meecrowave.testing.ConfigurationInject;
import org.awaitility.Awaitility;
import org.eclipse.jetty.util.component.LifeCycle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MeecrowaveConfig
public class MeecrowaveServerTest {

  @ConfigurationInject private Meecrowave.Builder config;

  private Jsonb jsonb;

  private URI uri;

  @BeforeEach
  public void setUp() {
    this.jsonb = new JsonbProvider().getJsonbProvider();
    this.uri = URI.create("ws://localhost:" + this.config.getHttpPort() + "/");
  }

  @AfterEach
  public void shutDown() throws Exception {
    this.jsonb.close();
  }

  @Test
  public void readInfo() throws Exception {
    // given:
    final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    final DefaultClientEndpoint clientEndpoint = new DefaultClientEndpoint();

    // when:
    try (final Session session = container.connectToServer(clientEndpoint, this.uri)) {
      final Usb2SnesRequest request =
          new Usb2SnesRequest("DeviceList", "SNES", emptyList(), emptyList());
      session.getBasicRemote().sendObject(request);
      Awaitility.await()
          .atMost(Duration.ofSeconds(1L))
          .until(() -> clientEndpoint.getMessages().size() > 0);

      session.getBasicRemote().sendObject(request);
      Awaitility.await()
          .atMost(Duration.ofSeconds(1L))
          .until(() -> clientEndpoint.getMessages().size() > 1);

      // Close session
      session.close();
    } finally {
      LifeCycle.stop(container);
    }

    // then:
    assertEquals(2, clientEndpoint.getMessages().size());
  }
}
