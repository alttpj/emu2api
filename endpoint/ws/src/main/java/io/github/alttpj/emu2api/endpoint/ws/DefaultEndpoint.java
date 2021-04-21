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

import io.github.alttpj.emu2api.endpoint.ws.data.Usb2SnesRequest;
import io.github.alttpj.emu2api.endpoint.ws.encoder.Usb2SnesRequestEncoder;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Implementation of the usb2snes protocol.
 *
 * <p>See <a
 * href="https://github.com/Skarsnik/QUsb2snes/blob/d11c5749d6c27879552a06d7a858eb228491bd69/docs/Procotol.md">Protocol.md</a>.
 */
@ServerEndpoint(
    value = "/",
    decoders = {Usb2SnesRequestEncoder.class},
    encoders = {Usb2SnesRequestEncoder.class})
public class DefaultEndpoint {

  @OnOpen
  public void onOpen(final Session session) throws IOException {
    // Get session and WebSocket connection
  }

  @OnMessage
  public void onMessage(final Session session, final Usb2SnesRequest message) throws IOException {
    // Handle new messages
  }

  @OnClose
  public void onClose(final Session session) throws IOException {
    // WebSocket connection closes
  }

  @OnError
  public void onError(final Session session, final Throwable throwable) {
    // Do error handling here
  }
}
