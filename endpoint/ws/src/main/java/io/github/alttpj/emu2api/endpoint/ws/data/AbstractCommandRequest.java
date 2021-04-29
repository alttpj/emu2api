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

import io.github.alttpj.emu2api.event.api.CommandRequest;
import io.github.alttpj.emu2api.event.api.CommandResponse;
import io.github.alttpj.emu2api.event.api.CommandType;
import io.github.alttpj.emu2api.event.api.RequestId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCommandRequest implements CommandRequest {

  private final RequestId requestId;
  private final CommandType commandType;
  private final List<String> commandParameters;
  private final String targetDevice;

  private final Map<String, CommandResponse> responses;

  public AbstractCommandRequest(
      final CommandType type, final List<String> commandParameters, final String targetDevice) {
    this.requestId = RequestId.create();
    this.commandParameters = commandParameters;
    this.commandType = type;
    this.targetDevice = targetDevice;

    this.responses = new ConcurrentHashMap<>();
  }

  public AbstractCommandRequest(final CommandType type, final List<String> commandParameters) {
    this(type, List.copyOf(commandParameters), null);
  }

  public AbstractCommandRequest(final CommandType type) {
    this(type, List.of(), null);
  }

  @Override
  public RequestId getRequestId() {
    return this.requestId;
  }

  @Override
  public CommandType getCommandType() {
    return this.commandType;
  }

  @Override
  public List<String> getCommandParameters() {
    return unmodifiableList(this.commandParameters);
  }

  @Override
  public Optional<String> getTargetDevice() {
    return Optional.ofNullable(this.targetDevice);
  }

  public Map<String, CommandResponse> getResponses() {
    return this.responses;
  }

  @Override
  public void addReturnParameters(final String name, final CommandResponse commandResponse) {
    this.responses.put(name, commandResponse);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", AbstractCommandRequest.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("requestId=" + this.requestId)
        .add("commandType=" + this.commandType)
        .add("commandParameters=" + this.commandParameters)
        .add("targetDevice='" + this.targetDevice + "'")
        .add("responses=" + this.responses)
        .toString();
  }
}
