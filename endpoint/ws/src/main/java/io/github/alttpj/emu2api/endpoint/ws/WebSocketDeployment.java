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

package io.github.alttpj.emu2api.endpoint.ws;

import io.github.alttpj.emu2api.endpoint.ws.json.Usb2SnesRequestDecoder;
import io.github.alttpj.emu2api.endpoint.ws.json.Usb2SnesRequestEncoder;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.server.ServerContainer;
import jakarta.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.logging.Logger;

@WebListener
public class WebSocketDeployment implements ServletContextListener {

  private static final Logger LOG = Logger.getLogger(WebSocketDeployment.class.getName());

  @Override
  public void contextInitialized(final ServletContextEvent sce) {
    final ServerContainer sc =
        (ServerContainer) sce.getServletContext().getAttribute(ServerContainer.class.getName());

    try {
      sc.addEndpoint(
          ServerEndpointConfig.Builder.create(DefaultServerEndpoint.class, "/")
              .decoders(List.of(Usb2SnesRequestDecoder.class))
              .encoders(List.of(Usb2SnesRequestEncoder.class))
              .configurator(
                  new ServerEndpointConfig.Configurator() {
                    @Override
                    public <T> T getEndpointInstance(final Class<T> clazz)
                        throws InstantiationException {
                      return CDI.current().select(clazz).get();
                    }
                  })
              .build());
    } catch (final DeploymentException jakartaWebsocketDeploymentException) {
      throw new RuntimeException(jakartaWebsocketDeploymentException);
    }
  }
}
