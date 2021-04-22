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

package io.github.alttpj.emu2api.source.config;

import io.github.alttpj.emu2api.source.api.config.EmulatorConfig;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.tomlj.TomlParseResult;

public class TomlEmulatorConfig extends ModifiedTomlFileReader {

  private static final Logger LOG = Logger.getLogger(TomlEmulatorConfig.class.getCanonicalName());

  private SimpleGeneralConfig generalConfig;

  private final Map<String, EmulatorConfig> emulatorConfigMap = new ConcurrentHashMap<>(5);

  public TomlEmulatorConfig() {
    this(null);
  }

  public TomlEmulatorConfig(final Path tomlConfigLocation) {
    super(tomlConfigLocation);
  }

  public SimpleGeneralConfig getGeneralConfig() {
    this.ensureGeneralConfig();

    return this.generalConfig;
  }

  protected void ensureGeneralConfig() {
    if (this.generalConfig != null && !this.needsUpdate()) {
      return;
    }

    final TomlParseResult result = this.readToml("general");

    this.generalConfig =
        new SimpleGeneralConfig(
            result.getBoolean("isDebug", () -> Boolean.FALSE),
            Optional.ofNullable(result.getString("logfile")).map(Paths::get).orElse(null));
  }

  public EmulatorConfig getEmulatorConfig(final String emulatorSourceSectionName) {
    if (emulatorSourceSectionName == null || emulatorSourceSectionName.isBlank()) {
      return new SimpleEmulatorConfig(false, Map.of());
    }

    this.ensureEmulatorConfigMap(emulatorSourceSectionName);

    return this.emulatorConfigMap.get(emulatorSourceSectionName);
  }

  private void ensureEmulatorConfigMap(final String emulatorSourceSectionName) {
    if (this.emulatorConfigMap.get(emulatorSourceSectionName) != null && !this.needsUpdate()) {
      return;
    }

    final TomlParseResult tomlParseResult = this.readToml(emulatorSourceSectionName);

    final String tomlSectionName = "emulators." + emulatorSourceSectionName;
    final Map<String, Object> configMap =
        (Map<String, Object>) tomlParseResult.get(tomlSectionName);

    final boolean enabled =
        tomlParseResult.getBoolean(tomlSectionName + ".isEnabled", () -> Boolean.TRUE);
    final EmulatorConfig simpleEmulatorConfig =
        new SimpleEmulatorConfig(enabled, Optional.ofNullable(configMap).orElseGet(Map::of));

    this.emulatorConfigMap.put(emulatorSourceSectionName, simpleEmulatorConfig);
  }
}
