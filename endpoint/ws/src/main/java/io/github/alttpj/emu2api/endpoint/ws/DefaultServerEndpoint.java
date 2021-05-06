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

import io.github.alttpj.emu2api.endpoint.ws.data.CallbackCommandRequest;
import io.github.alttpj.emu2api.endpoint.ws.data.SessionInfo;
import io.github.alttpj.emu2api.endpoint.ws.data.Usb2SnesRequest;
import io.github.alttpj.emu2api.endpoint.ws.data.Usb2SnesResult;
import io.github.alttpj.emu2api.event.api.Command;
import io.github.alttpj.emu2api.event.api.CommandRequest;
import io.github.alttpj.emu2api.event.api.CommandType;
import io.github.alttpj.emu2api.utils.async.ExecutorName;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.NotificationOptions;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.RemoteEndpoint.Async;
import jakarta.websocket.Session;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the usb2snes protocol.
 *
 * <p>See <a
 * href="https://github.com/Skarsnik/QUsb2snes/blob/d11c5749d6c27879552a06d7a858eb228491bd69/docs/Procotol.md">Protocol.md</a>.
 */
@Dependent
public class DefaultServerEndpoint {

  private static final Logger LOG =
      Logger.getLogger(DefaultServerEndpoint.class.getCanonicalName());

  private static final Map<Session, SessionInfo> SESSION_INFO = new ConcurrentHashMap<>();

  @Inject private Event<CommandRequest> commandEvent;

  @Inject
  @ExecutorName("concurrent/frontend")
  private ExecutorService executorService;

  @OnOpen
  public void onOpen(final Session session) throws IOException {
    // Get session and WebSocket connection
    SESSION_INFO.put(session, new SessionInfo(session));
  }

  @OnMessage
  public void onMessage(final Session session, final Usb2SnesRequest message) throws IOException {
    LOG.log(Level.INFO, "received raw message: " + message);
    final CommandType commandType = CommandType.getByOpCode(message.getOpcode());
    final CallbackCommandRequest commandRequest =
        new CallbackCommandRequest(
            session,
            commandType,
            message.getOperands(),
            SESSION_INFO.get(session).getAttachedToDeviceName().orElse(null));
    commandRequest.register(this::onResponse);

    LOG.log(
        Level.FINE,
        () ->
            String.format(
                Locale.ENGLISH,
                "Received command, type=[%s], parms=[%s], target=[%s]",
                commandRequest.getCommandType(),
                commandRequest.getCommandParameters(),
                commandRequest.getTargetDevice()));

    this.commandEvent
        .select(new Command.Literal(commandRequest.getCommandType()))
        .fireAsync(commandRequest, NotificationOptions.ofExecutor(this.executorService))
        .thenAccept(CallbackCommandRequest::callAll);
  }

  @OnClose
  public void onClose(final Session session, final CloseReason closeReason) throws IOException {
    removePendingRequests(session);
    SESSION_INFO.remove(session);
  }

  @OnError
  public void onError(final Session session, final Throwable throwable) {
    LOG.log(Level.SEVERE, "unexpected error for session " + session, throwable);

    closeClientSession(session, throwable);
  }

  public void onResponse(final CallbackCommandRequest response) {
    final Session session = response.getSession();

    if (session == null) {
      return;
    }

    if (!response.isAllSuccesful()) {
      this.onError(session, response.getFirstFailedWith().orElseThrow());
      return;
    }

    if (!response.isReturnToClient()) {
      return;
    }

    final Future<Void> voidFuture = this.doSendResponse(response, session);
    CompletableFuture.supplyAsync(
            () -> {
              try {
                return voidFuture.get(500L, TimeUnit.MILLISECONDS);
              } catch (final InterruptedException
                  | ExecutionException
                  | TimeoutException javaLangInterruptedException) {
                throw new CompletionException(javaLangInterruptedException);
              }
            },
            this.executorService)
        .whenCompleteAsync(
            (result, error) -> {
              if (error != null) {
                LOG.log(Level.WARNING, "unable to complete request: ", error);
              }
            });
  }

  protected Future<Void> doSendResponse(
      final CallbackCommandRequest response, final Session session) {
    final Async asyncRemote = session.getAsyncRemote();
    asyncRemote.setSendTimeout(100L);

    if (response.isBinaryResponse()) {
      final ByteBuffer binaryResponse = response.getBinaryResponse();
      LOG.log(
          Level.INFO,
          () ->
              String.format(
                  Locale.ENGLISH,
                  "sending response [%s] to session [%s].",
                  new BigInteger(1, binaryResponse.array()).toString(16),
                  session));
      return asyncRemote.sendBinary(binaryResponse);
    }

    final Usb2SnesResult usb2SnesResult = new Usb2SnesResult(response.getAggregatedResponses());

    LOG.log(
        Level.INFO,
        () ->
            String.format(
                Locale.ENGLISH, "sending response [%s] to session [%s].", usb2SnesResult, session));

    return asyncRemote.sendObject(usb2SnesResult);
  }

  public void onAttach(
      final @ObservesAsync @Command(type = CommandType.ATTACH) CallbackCommandRequest attach) {
    final String attachTo = (String) attach.getCommandParameters().get(0);
    SESSION_INFO.get(attach.getSession()).setAttachedToDeviceName(attachTo);
  }

  public void onName(
      final @ObservesAsync @Command(type = CommandType.NAME) CallbackCommandRequest nameRequest) {
    SESSION_INFO
        .get(nameRequest.getSession())
        .setClientName((String) nameRequest.getCommandParameters().get(0));
  }

  private static void removePendingRequests(final Session session) {
    SESSION_INFO.remove(session);
  }

  private static void closeClientSession(final Session session, final Throwable throwable) {
    final String reasonPhrase =
        Optional.ofNullable(throwable.getMessage()).orElseGet(() -> throwable.getClass().getName());

    final CloseReason closeReason = new CloseReason(CloseCodes.CLOSED_ABNORMALLY, reasonPhrase);
    try {
      session.close(closeReason);
      removePendingRequests(session);
    } catch (final IOException ioException) {
      throw new UncheckedIOException(ioException);
    }
  }
}
