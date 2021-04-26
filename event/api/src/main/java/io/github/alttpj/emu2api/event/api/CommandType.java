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

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.StringJoiner;

public enum CommandType {
  /** Queries the emulators for recognized devices. */
  DEVICE_LIST("DeviceList", List.of());

  private final String opcode;
  private final List<Class<?>> parameterTypes;

  CommandType(final String opcode, final List<Class<?>> parameterTypes) {
    this.opcode = opcode;
    this.parameterTypes = unmodifiableList(parameterTypes);
  }

  public String getOpcode() {
    return this.opcode;
  }

  public List<Class<?>> getParameterTypes() {
    return this.parameterTypes;
  }

  public static Optional<CommandType> getByOpCode(final String opcode) {
    return Arrays.stream(values()).filter(ct -> opcodeMatchesIgnoreCase(ct, opcode)).findAny();
  }

  private static boolean opcodeMatchesIgnoreCase(
      final CommandType commandType, final String opcode) {
    requireNonNull(opcode, "opcode");
    return commandType
        .opcode
        .toLowerCase(Locale.ENGLISH)
        .equals(opcode.toLowerCase(Locale.ENGLISH));
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CommandType.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("parameterTypes=" + this.parameterTypes)
        .toString();
  }
}
