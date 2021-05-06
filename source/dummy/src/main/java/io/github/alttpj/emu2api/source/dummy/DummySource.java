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

package io.github.alttpj.emu2api.source.dummy;

import io.github.alttpj.emu2api.source.api.EmulatorSource;
import jakarta.enterprise.context.ApplicationScoped;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.StringJoiner;

@ApplicationScoped
public class DummySource implements EmulatorSource {

  private static final String SOURCE_NAME = "DummySource";

  @Override
  public String getSourceName() {
    return SOURCE_NAME;
  }

  @Override
  public List<String> getDiscoveredDeviceNames() {
    return List.of(SOURCE_NAME + "_1");
  }

  @Override
  public boolean hasDevice(final String wantedDeviceName) {
    return this.getDiscoveredDeviceNames().contains(wantedDeviceName);
  }

  @Override
  public List<String> getInfo(final String deviceName, final List<String> commandParameters) {
    // TODO: implement
    throw new UnsupportedOperationException(
        "not yet implemented: [io.github.alttpj.emu2api.source.dummy.DummySource::getInfo].");
  }

  @Override
  public ByteBuffer getAddr(final String deviceName, final List<String> commandParameters) {
    // TODO: implement
    throw new UnsupportedOperationException(
        "not yet implemented: [io.github.alttpj.emu2api.source.dummy.DummySource::getAddr].");
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", DummySource.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .toString();
  }
}
