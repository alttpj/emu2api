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

package io.github.alttpj.emu2api.endpoint.ws;

import io.github.alttpj.emu2api.endpoint.ws.data.Usb2SnesRequest;
import io.github.alttpj.emu2api.endpoint.ws.data.Usb2SnesResult;
import io.github.alttpj.emu2api.event.api.Command;
import io.github.alttpj.emu2api.event.api.CommandRequest;
import io.github.alttpj.emu2api.event.api.CommandResponse;
import io.github.alttpj.emu2api.event.api.CommandType;
import io.github.alttpj.emu2api.event.api.RequestId;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the usb2snes protocol.
 *
 * <p>See <a
 * href="https://github.com/Skarsnik/QUsb2snes/blob/d11c5749d6c27879552a06d7a858eb228491bd69/docs/Procotol.md">Protocol.md</a>.
 */
@Dependent
@Default
public class DefaultServerEndpoint {

  private static final Logger LOG =
      Logger.getLogger(DefaultServerEndpoint.class.getCanonicalName());

  // TODO: make sure they are also removed after a specific amount of time.
  private static final Map<RequestId, Session> PENDING_REQUESTS = new ConcurrentHashMap<>();

  @Inject private Event<CommandRequest> commandEvent;

  @OnOpen
  public void onOpen(final Session session) throws IOException {
    // Get session and WebSocket connection
  }

  @OnMessage
  public void onMessage(final Session session, final Usb2SnesRequest message) throws IOException {
    final CommandType commandType = CommandType.getByOpCode(message.getOpcode()).orElseThrow();
    final CommandRequest commandRequest = CommandRequest.builder().commandType(commandType).build();
    PENDING_REQUESTS.put(commandRequest.getRequestId(), session);

    this.commandEvent
        .select(new Command.Literal(commandRequest.getCommandType()))
        .fire(commandRequest);
  }

  @OnClose
  public void onClose(final Session session, final CloseReason closeReason) throws IOException {
    removePendingRequests(session);
  }

  @OnError
  public void onError(final Session session, final Throwable throwable) {
    LOG.log(Level.SEVERE, "unexpected error for session " + session, throwable);

    try {
      closeClientSession(session, throwable);
    } catch (final IOException javaIoException) {
      LOG.log(Level.WARNING, "unable to close the session " + session, javaIoException);
    }
  }

  public void onResponse(final @Observes CommandResponse response) throws IOException {
    final RequestId requestId = response.getRequestId();
    final Session session = PENDING_REQUESTS.get(requestId);

    if (session == null) {
      return;
    }

    if (!response.isSuccessful()) {
      closeClientSession(session, response.getFailedWith().orElseThrow());
      return;
    }

    try {
      final Usb2SnesResult usb2SnesResult = new Usb2SnesResult(response.getReturnParameters());
      session.getBasicRemote().sendObject(usb2SnesResult);
    } catch (final EncodeException jakartaWebsocketEncodeException) {
      LOG.log(Level.SEVERE, "Unexpected encoding exception", jakartaWebsocketEncodeException);
      closeClientSession(session, jakartaWebsocketEncodeException);
    }
  }

  private static void removePendingRequests(final Session session) {
    PENDING_REQUESTS.entrySet().stream()
        .filter(entry -> entry.getValue().equals(session))
        .map(Entry::getKey)
        .forEach(PENDING_REQUESTS::remove);
  }

  private static void closeClientSession(final Session session, final Throwable throwable)
      throws IOException {
    final String reasonPhrase =
        Optional.ofNullable(throwable.getMessage()).orElseGet(() -> throwable.getClass().getName());

    final CloseReason closeReason = new CloseReason(CloseCodes.CLOSED_ABNORMALLY, reasonPhrase);
    session.close(closeReason);
    removePendingRequests(session);
  }
}
