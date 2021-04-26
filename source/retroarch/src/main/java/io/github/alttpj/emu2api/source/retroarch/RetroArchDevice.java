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

package io.github.alttpj.emu2api.source.retroarch;

import io.github.alttpj.emu2api.utils.ulid.ULID;
import java.util.Objects;
import java.util.StringJoiner;

public class RetroArchDevice {

  private final ULID deviceId;

  private final String name;

  private final String host;

  private final int port;

  private final String transport;

  public RetroArchDevice(
      final String name, final String host, final int port, final String transport) {
    this.deviceId = ULID.nextULID();
    this.name = name;
    this.host = host;
    this.port = port;
    this.transport = transport;
  }

  // getter

  public ULID getDeviceId() {
    return this.deviceId;
  }

  public String getName() {
    return this.name;
  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  public String getTransport() {
    return this.transport;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }
    final RetroArchDevice that = (RetroArchDevice) other;
    return this.port == that.port
        && this.host.equals(that.host)
        && this.transport.equals(that.transport);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.host, this.port, this.transport);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", RetroArchDevice.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("deviceId=" + this.deviceId)
        .add("name='" + this.name + "'")
        .add("host='" + this.host + "'")
        .add("port=" + this.port)
        .add("transport='" + this.transport + "'")
        .toString();
  }
}
