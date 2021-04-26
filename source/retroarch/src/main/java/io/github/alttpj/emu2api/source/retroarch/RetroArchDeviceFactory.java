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

import jakarta.enterprise.context.Dependent;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Dependent
public class RetroArchDeviceFactory {

  public RetroArchDeviceFactory() {}

  public RetroArchDevice build(final Map<String, Object> instance) {
    final String name =
        (String)
            Optional.ofNullable(instance.get("name")).orElseThrow(() -> this.missingField("name"));
    final String host =
        (String)
            Optional.ofNullable(instance.get("host")).orElseThrow(() -> this.missingField("host"));
    final int port = (int) instance.getOrDefault("port", 55355);
    final String transport = (String) instance.getOrDefault("transport", "udp");

    return new RetroArchDevice(name, host, port, transport);
  }

  public Map<String, Object> getDefaultConfigurationMap() {
    return Map.of(
        "name", "localhost",
        "host", "localhost",
        "port", 55355,
        "transport", "udp");
  }

  private IllegalArgumentException missingField(final String fieldname) {
    final String message =
        String.format(Locale.ENGLISH, "instance must contain key '%s'!", fieldname);
    return new IllegalArgumentException(message);
  }
}
