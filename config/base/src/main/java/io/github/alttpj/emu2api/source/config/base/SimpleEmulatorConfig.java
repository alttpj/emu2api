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

package io.github.alttpj.emu2api.source.config.base;

import static java.util.Collections.unmodifiableMap;

import java.util.Map;
import java.util.StringJoiner;

public class SimpleEmulatorConfig implements EmulatorConfig {

  private final boolean enabled;
  private final Map<String, Object> config;

  public SimpleEmulatorConfig(final boolean isEnabled, final Map<String, Object> config) {
    this.enabled = isEnabled;
    this.config = config;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public Map<String, Object> getConfigurationMap() {
    return unmodifiableMap(this.config);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", SimpleEmulatorConfig.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("enabled=" + this.enabled)
        .add("config=" + this.config)
        .toString();
  }
}
