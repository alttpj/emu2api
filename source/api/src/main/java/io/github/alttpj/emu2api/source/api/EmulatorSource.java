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

package io.github.alttpj.emu2api.source.api;

import java.util.List;
import java.util.Set;

/** An emulator source is an implementation of a backend, like RetroArch or sd2snes. */
public interface EmulatorSource {

  /**
   * Returns the name of this source.
   *
   * @return the name of this source.
   */
  String getSourceName();

  /**
   * Returns a set of discovered device names.
   *
   * <p><strong>Implementation hint:</strong><br>
   * It is a good practive to add a prefix. E.g. if the emulator's configured name is 'localhost',
   * then this should return a list containing 'SourceName_localhost'.
   *
   * <p><strong>Timing:</strong><br>
   * The method is expected to return within 200ms. Otherwise the results may not be used.
   *
   * @return a set of prefixed emulator names.
   */
  Set<String> getDiscoveredDeviceNames();

  boolean hasDevice(String wantedDeviceName);

  Set<String> getInfo(String deviceName, List<String> commandParameters);
}
