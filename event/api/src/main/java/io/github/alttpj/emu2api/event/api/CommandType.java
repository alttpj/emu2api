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
  DEVICE_LIST("DeviceList", List.of()),
  /** Attaches to a given device. */
  ATTACH("Attach", List.of(String.class), false),
  /** Sets a name to the session. */
  NAME("Name", List.of(String.class), false),
  /** Infos about devices, e.g. version, internal device name by the source itself, etc. */
  INFO("Info", List.of()),
  /**
   * GetAddress [offset, size].
   *
   * <p>To read memory from the device. Offset and size must be both encoded in hexadecimal. Offset
   * are not Snes address, they are specific on how usb2snes store the various information (more
   * later).
   *
   * <p>This read the first 256 bytes from the WRAM: {@code ["F50000", "100"]}.
   *
   * <p>See: <a
   * href="https://github.com/Skarsnik/QUsb2snes/blob/d11c5749d6c27879552a06d7a858eb228491bd69/docs/Procotol.md#usb2snes-address">Protocol.md#Usb2snes-address</a>
   */
  GET_ADDRESS("GetAddress", List.of(String.class, String.class), true, true),
  /** PutAddress [offset, size]. */
  PUT_ADDRESS("PutAddress", List.of(String.class, String.class));

  private final String opcode;
  private final List<Class<?>> parameterTypes;
  private final boolean returnToClient;
  private final boolean isBinaryResponse;

  CommandType(final String opcode, final List<Class<?>> parameterTypes) {
    this.opcode = opcode;
    this.parameterTypes = unmodifiableList(parameterTypes);
    this.returnToClient = true;
    this.isBinaryResponse = false;
  }

  CommandType(
      final String opcode, final List<Class<?>> parameterTypes, final boolean returnToClient) {
    this.opcode = opcode;
    this.parameterTypes = unmodifiableList(parameterTypes);
    this.returnToClient = returnToClient;
    this.isBinaryResponse = false;
  }

  CommandType(
      final String opcode,
      final List<Class<?>> parameterTypes,
      final boolean returnToClient,
      final boolean isBinaryResponse) {
    this.opcode = opcode;
    this.parameterTypes = unmodifiableList(parameterTypes);
    this.returnToClient = returnToClient;
    this.isBinaryResponse = isBinaryResponse;
  }

  public String getOpcode() {
    return this.opcode;
  }

  public List<Class<?>> getParameterTypes() {
    return this.parameterTypes;
  }

  public static Optional<CommandType> getByOpCodeOpt(final String opcode) {
    return Arrays.stream(values()).filter(ct -> opcodeMatchesIgnoreCase(ct, opcode)).findAny();
  }

  public static CommandType getByOpCode(final String opcode) {
    return getByOpCodeOpt(opcode)
        .orElseThrow(
            () ->
                new UnsupportedOperationException(
                    "Unknown opcode: [" + opcode + "]. Not implemented?!"));
  }

  public boolean isReturnToClient() {
    return this.returnToClient;
  }

  public boolean isBinaryResponse() {
    return this.isBinaryResponse;
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
