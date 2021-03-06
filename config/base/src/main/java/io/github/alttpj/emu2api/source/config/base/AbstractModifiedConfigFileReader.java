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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractModifiedConfigFileReader {

  private static final Logger LOG =
      Logger.getLogger(AbstractModifiedConfigFileReader.class.getCanonicalName());

  private Instant lastReadTime = Instant.EPOCH;

  private final Path tomlConfigFileLocation;
  private GeneralConfig generalConfig;
  private final Map<String, EmulatorConfig> emulatorConfigMap = new ConcurrentHashMap<>(5);

  public AbstractModifiedConfigFileReader() {
    this(null);
  }

  public AbstractModifiedConfigFileReader(final Path tomlConfigFileLocation) {
    this.tomlConfigFileLocation = tomlConfigFileLocation;
  }

  protected Path getConfigFilePath() {
    if (this.tomlConfigFileLocation != null) {
      return this.tomlConfigFileLocation;
    }

    final Path userHome = Paths.get(System.getProperty("user.home"));
    final Path emu2apiToml = Paths.get("./config/emu2api/emu2api." + this.getFileExtension());

    return userHome.resolve(emu2apiToml);
  }

  protected boolean needsUpdate() {
    try {
      final Instant lastModifiedTime =
          Files.getLastModifiedTime(this.getConfigFilePath()).toInstant();
      return lastModifiedTime.isAfter(this.lastReadTime);
    } catch (final IOException ioException) {
      return false;
    }
  }

  /**
   * Let this return &quot;{@code toml}&quot; for a toml file and &quot;{@code yaml}&quot; for a
   * yaml file.
   *
   * @return the file extension without leading dot.
   */
  protected abstract String getFileExtension();

  /**
   * Checks if the config file neeeds to be updated.
   *
   * <p>Sets the last read time if needed.
   *
   * @param reason the reason why we read the file. Just pass in "general" or the emulator name
   *     which was requested.
   * @return an Map which contains the configuration.
   */
  protected Map<String, Object> readConfigFile(final String reason) {
    LOG.log(
        Level.FINE,
        "reading file "
            + this.getConfigFilePath().toFile().getAbsolutePath()
            + " because of "
            + reason);
    final Path configSource = this.getConfigFilePath();

    if (!Files.exists(configSource) || !Files.isReadable(configSource)) {
      return this.emptyConfigFile();
    }

    try {
      final Map<String, Object> readConfigFile = this.doReadConfigFile(this.getConfigFilePath());
      this.lastReadTime = Instant.now();
      return readConfigFile;
    } catch (final IOException ioException) {
      LOG.log(Level.SEVERE, "Cannot read config", ioException);
    }

    return this.emptyConfigFile();
  }

  protected abstract Map<String, Object> doReadConfigFile(Path configFilePath) throws IOException;

  protected Map<String, Object> emptyConfigFile() {
    return Map.of();
  }

  public GeneralConfig getGeneralConfig() {
    this.ensureGeneralConfig();

    return this.generalConfig;
  }

  protected void ensureGeneralConfig() {
    if (this.generalConfig != null && !this.needsUpdate()) {
      return;
    }

    this.generalConfig = this.doReadGeneralConfig();
  }

  protected abstract GeneralConfig doReadGeneralConfig();

  public EmulatorConfig getEmulatorConfig(final String emulatorSourceSectionName) {
    if (emulatorSourceSectionName == null || emulatorSourceSectionName.isBlank()) {
      return new SimpleEmulatorConfig(false, Map.of());
    }

    this.ensureEmulatorConfigMap(emulatorSourceSectionName);

    return this.emulatorConfigMap.get(emulatorSourceSectionName);
  }

  private void ensureEmulatorConfigMap(final String emulatorSectionName) {
    if (this.emulatorConfigMap.get(emulatorSectionName) != null && !this.needsUpdate()) {
      return;
    }

    this.emulatorConfigMap.put(emulatorSectionName, this.doReadEmulatorConfig(emulatorSectionName));
  }

  protected abstract EmulatorConfig doReadEmulatorConfig(String emulatorSectionName);
}
