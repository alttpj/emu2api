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

package io.github.alttpj.emu2api.event.api;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

public interface CommandRequest {

  RequestId getRequestId();

  CommandType getCommandType();

  List<String> getCommandParameters();

  Optional<String> getTargetDevice();

  boolean isBinaryResponse();

  ByteBuffer getBinaryResponse();

  void setBinaryResponse(final ByteBuffer byteBuffer);

  void addReturnParameters(final String name, final CommandResponse commandResponse);
}
