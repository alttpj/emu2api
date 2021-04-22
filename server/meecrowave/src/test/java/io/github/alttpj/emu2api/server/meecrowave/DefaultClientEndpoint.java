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

package io.github.alttpj.emu2api.server.meecrowave;

import static java.util.Collections.unmodifiableList;

import io.github.alttpj.emu2api.endpoint.ws.json.Usb2SnesRequestEncoder;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Implementation of the usb2snes protocol.
 *
 * <p>See <a
 * href="https://github.com/Skarsnik/QUsb2snes/blob/d11c5749d6c27879552a06d7a858eb228491bd69/docs/Procotol.md">Protocol.md</a>.
 */
@ClientEndpoint(encoders = {Usb2SnesRequestEncoder.class})
public class DefaultClientEndpoint {

  private static final Logger LOG =
      Logger.getLogger(DefaultClientEndpoint.class.getCanonicalName());

  private final List<String> messages = new CopyOnWriteArrayList<>();

  @OnOpen
  public void onOpen(final Session session) throws IOException {
    // Get session and WebSocket connection
  }

  @OnMessage
  public void onMessage(final Session session, final String message) throws IOException {
    this.messages.add(message);
  }

  @OnClose
  public void onClose(final Session session, final CloseReason closeReason) throws IOException {}

  @OnError
  public void onError(final Session session, final Throwable throwable) {
    throw (RuntimeException) throwable;
  }

  public List<String> getMessages() {
    return unmodifiableList(this.messages);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", DefaultClientEndpoint.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("messages=" + this.messages)
        .toString();
  }
}
