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

package io.github.alttpj.emu2api.source.config.toml;

import static java.util.Collections.emptyMap;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import io.github.alttpj.emu2api.source.config.base.AbstractModifiedConfigFileReader;
import io.github.alttpj.emu2api.source.config.base.EmulatorConfig;
import io.github.alttpj.emu2api.source.config.base.GeneralConfig;
import io.github.alttpj.emu2api.source.config.base.SimpleEmulatorConfig;
import io.github.alttpj.emu2api.source.config.base.SimpleGeneralConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class TomlEmulatorConfig extends AbstractModifiedConfigFileReader {

  private static final Logger LOG = Logger.getLogger(TomlEmulatorConfig.class.getCanonicalName());

  public TomlEmulatorConfig() {
    this(null);
  }

  public TomlEmulatorConfig(final Path tomlConfigLocation) {
    super(tomlConfigLocation);
  }

  @Override
  protected String getFileExtension() {
    return "toml";
  }

  @Override
  protected Map<String, Object> doReadConfigFile(final Path configSource) throws IOException {
    final TomlMapper toml = TomlMapper.builder().build();

    try (final var fis = Files.newInputStream(configSource)) {
      final Map<String, Object> jsonNode = toml.readValue(fis, Map.class);
      return jsonNode;
    }
  }

  @Override
  protected Map<String, Object> emptyConfigFile() {
    return emptyMap();
  }

  @Override
  protected GeneralConfig doReadGeneralConfig() {
    final Map<String, Object> general = this.readConfigFile("general");
    final Map<String, Object> generalConfig =
        (Map<String, Object>) general.getOrDefault("general", Map.of());

    return new SimpleGeneralConfig(
        (boolean) generalConfig.getOrDefault("isDebug", false),
        Optional.ofNullable((String) generalConfig.get("logfile")).map(Paths::get).orElse(null));
  }

  @Override
  protected EmulatorConfig doReadEmulatorConfig(final String emulatorSourceSectionName) {
    final Map<String, Object> general = this.readConfigFile(emulatorSourceSectionName);
    final Map<String, Object> emulatorConfig =
        (Map<String, Object>) general.getOrDefault("emulators", Map.of());
    final Map<String, Object> emuConfig =
        (Map<String, Object>) emulatorConfig.getOrDefault(emulatorSourceSectionName, Map.of());

    final boolean enabled = (boolean) emuConfig.getOrDefault("enabled", true);
    return new SimpleEmulatorConfig(enabled, emuConfig);
  }
}
