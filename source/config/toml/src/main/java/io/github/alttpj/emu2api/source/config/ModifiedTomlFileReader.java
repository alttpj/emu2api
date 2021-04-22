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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

public class ModifiedTomlFileReader {

  private static final Logger LOG =
      Logger.getLogger(ModifiedTomlFileReader.class.getCanonicalName());

  private Instant lastReadTime = Instant.EPOCH;

  private final Path tomlConfigFileLocation;

  public ModifiedTomlFileReader() {
    this(null);
  }

  public ModifiedTomlFileReader(final Path tomlConfigFileLocation) {
    this.tomlConfigFileLocation = tomlConfigFileLocation;
  }

  private Path getTomlConfigPath() {
    if (this.tomlConfigFileLocation != null) {
      return this.tomlConfigFileLocation;
    }

    final Path userHome = Paths.get(System.getProperty("user.home"));
    final Path emu2apiToml = Paths.get("./config/emu2api/emu2api.toml");

    return userHome.resolve(emu2apiToml);
  }

  protected boolean needsUpdate() {
    try {
      final Instant lastModifiedTime =
          Files.getLastModifiedTime(this.getTomlConfigPath()).toInstant();
      return lastModifiedTime.isAfter(this.lastReadTime);
    } catch (final IOException ioException) {
      return false;
    }
  }

  protected TomlParseResult readToml(final String reason) {
    LOG.log(
        Level.FINE,
        "reading file "
            + this.getTomlConfigPath().toFile().getAbsolutePath()
            + " because of "
            + reason);
    final Path configSource = this.getTomlConfigPath();

    if (!Files.exists(configSource) || !Files.isReadable(configSource)) {
      return Toml.parse("");
    }

    try {
      final TomlParseResult result = Toml.parse(configSource);
      result.errors().forEach(error -> LOG.log(Level.WARNING, error.toString()));
      this.lastReadTime = Instant.now();

      return result;
    } catch (final IOException tomlParseException) {
      LOG.log(Level.SEVERE, "Cannot read toml config", tomlParseException);
    }

    return Toml.parse("");
  }
}
