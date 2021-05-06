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

package io.github.alttpj.emu2api.endpoint.ws.data;

import static java.util.Collections.unmodifiableList;

import io.github.alttpj.emu2api.event.api.CommandResponse;
import io.github.alttpj.emu2api.event.api.CommandType;
import jakarta.websocket.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CallbackCommandRequest extends AbstractCommandRequest {

  private final List<Consumer<CallbackCommandRequest>> callbacks;
  private final Session session;

  public CallbackCommandRequest(
      final Session session,
      final CommandType type,
      final List<String> commandParameters,
      final String targetDevice) {
    super(type, commandParameters, targetDevice);
    this.callbacks = new ArrayList<>();
    this.session = session;
  }

  public CallbackCommandRequest(
      final Session session, final CommandType type, final List<String> commandParameters) {
    this(session, type, commandParameters, null);
  }

  public CallbackCommandRequest(final Session session, final CommandType type) {
    this(session, type, List.of(), null);
  }

  public Session getSession() {
    return this.session;
  }

  public List<Consumer<CallbackCommandRequest>> getCallbacks() {
    return unmodifiableList(this.callbacks);
  }

  public void register(final Consumer<CallbackCommandRequest> consumer) {
    this.callbacks.add(consumer);
  }

  public void callAll() {
    this.callbacks.forEach(callback -> callback.accept(this));
  }

  public List<String> getAggregatedResponses() {
    return this.getResponses().values().stream()
        .map(CommandResponse::getReturnParameters)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  public boolean isAllSuccesful() {
    return this.getResponses().values().stream().allMatch(CommandResponse::isSuccessful);
  }

  public Optional<Throwable> getFirstFailedWith() {
    return this.getResponses().values().stream()
        .map(CommandResponse::getFailedWith)
        .flatMap(Optional::stream)
        .findFirst();
  }

  public boolean isReturnToClient() {
    return this.getCommandType().isReturnToClient();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CallbackCommandRequest.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("callbacks=" + this.callbacks)
        .add("session=" + this.session)
        .toString();
  }
}
