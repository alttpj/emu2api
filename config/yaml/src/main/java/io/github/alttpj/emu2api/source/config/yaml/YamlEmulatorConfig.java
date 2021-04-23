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

package io.github.alttpj.emu2api.source.config.yaml;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

public class YamlEmulatorConfig extends AbstractModifiedConfigFileReader {

  private static final Logger LOG = Logger.getLogger(YamlEmulatorConfig.class.getCanonicalName());

  private SimpleGeneralConfig generalConfig;

  private final Map<String, EmulatorConfig> emulatorConfigMap = new ConcurrentHashMap<>(5);

  public YamlEmulatorConfig() {
    this(null);
  }

  public YamlEmulatorConfig(final Path tomlConfigLocation) {
    super(tomlConfigLocation);
  }

  @Override
  protected String getFileExtension() {
    return "yaml";
  }

  @Override
  protected Map<String, Object> doReadConfigFile(final Path configFilePath) throws IOException {
    final Yaml yaml = new Yaml();

    try (final var configIs = Files.newInputStream(configFilePath)) {
      return yaml.load(configIs);
    }
  }

  @Override
  protected Map<String, Object> emptyConfigFile() {
    return Map.of();
  }

  @Override
  public SimpleGeneralConfig getGeneralConfig() {
    this.ensureGeneralConfig();

    return this.generalConfig;
  }

  @Override
  protected GeneralConfig doReadGeneralConfig() {
    final Map<String, Object> general = this.readConfigFile("general");
    final Map<String, Object> generalConfig =
        (Map<String, Object>) general.getOrDefault("general", Map.of());

    return new SimpleGeneralConfig(
        (boolean) generalConfig.getOrDefault("isDebug", false),
        Optional.ofNullable((String) generalConfig.get("logfile"))
            .map(Paths::get)
            .orElse(null));
  }

  @Override
  protected EmulatorConfig doReadEmulatorConfig(final String emulatorSectionName) {
    final Map<String, Object> general = this.readConfigFile(emulatorSectionName);
    final Map<String, Object> emulatorConfig =
        (Map<String, Object>) general.getOrDefault("emulators", Map.of());
    final Map<String, Object> emuConfig =
        (Map<String, Object>) emulatorConfig.getOrDefault(emulatorSectionName, Map.of());

    final boolean enabled = (boolean) emuConfig.getOrDefault("enabled", true);
    return new SimpleEmulatorConfig(enabled, emuConfig);
  }
}
