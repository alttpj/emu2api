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

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Usb2SnesRequest {

  @JsonbProperty("Opcode")
  private String opcode;

  @JsonbProperty("Space")
  private String space;

  @JsonbProperty("Flags")
  private List<String> flags = List.of();

  @JsonbProperty("Operands")
  private List<String> operands = List.of();

  public Usb2SnesRequest() {
    // default constructor
  }

  public Usb2SnesRequest(
      final @JsonbProperty("Opcode") String opcode,
      final @JsonbProperty("Space") String space,
      final @JsonbProperty("Flags") List<String> flags,
      final @JsonbProperty("Operands") List<String> operands) {

    this.opcode = opcode;
    this.space = space;
    this.flags = Collections.unmodifiableList(flags);
    this.operands = Collections.unmodifiableList(operands);
  }

  public Usb2SnesRequest(
      final @JsonbProperty("Opcode") String opcode, final @JsonbProperty("Space") String space) {

    this.opcode = opcode;
    this.space = space;
    this.flags = List.of();
    this.operands = List.of();
  }

  @JsonbProperty("Opcode")
  public String getOpcode() {
    return this.opcode;
  }

  @JsonbProperty("Space")
  public String getSpace() {
    return this.space;
  }

  @JsonbProperty("Flags")
  public List<String> getFlags() {
    return this.flags;
  }

  @JsonbProperty("Operands")
  public List<String> getOperands() {
    return this.operands;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }
    final Usb2SnesRequest that = (Usb2SnesRequest) other;
    return this.opcode.equals(that.opcode)
        && this.space.equals(that.space)
        && this.flags.equals(that.flags)
        && this.operands.equals(that.operands);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.opcode, this.space, this.flags, this.operands);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Usb2SnesRequest.class.getSimpleName() + "[", "]")
        .add("opcode='" + this.opcode + "'")
        .add("space='" + this.space + "'")
        .add("flags=" + this.flags)
        .add("operands=" + this.operands)
        .toString();
  }
}
