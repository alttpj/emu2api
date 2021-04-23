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

import io.github.alttpj.emu2api.source.config.base.Emulator;
import io.github.alttpj.emu2api.source.config.base.EmulatorConfig;
import io.github.alttpj.emu2api.source.config.base.GeneralConfig;
import io.github.alttpj.emu2api.source.config.base.PropertyKey;
import io.github.alttpj.emu2api.source.config.base.SystemProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import java.nio.file.Paths;

@ApplicationScoped
public class TomlConfigProducer {

  @Inject
  @PropertyKey(name = "emu2snes.configfile")
  private SystemProperty configFileLocation;

  @Produces
  @ApplicationScoped
  public TomlEmulatorConfig produceTomlEmulatorConfig() {
    if (this.configFileLocation.getValue() != null) {
      return new TomlEmulatorConfig(Paths.get(this.configFileLocation.getValue()));
    }

    return new TomlEmulatorConfig();
  }

  @Produces
  @Emulator
  public EmulatorConfig produceEmulatorConfig(
      final InjectionPoint injectionPoint, final TomlEmulatorConfig tomlEmulatorConfig) {
    final Annotated annotated = injectionPoint.getAnnotated();

    if (!annotated.isAnnotationPresent(Emulator.class)) {
      throw new IllegalArgumentException(
          "@Inject EmulatorConfig needs @Emulator(name='') qualifier!");
    }

    final String emulator = annotated.getAnnotation(Emulator.class).name();

    return tomlEmulatorConfig.getEmulatorConfig(emulator);
  }

  @Produces
  public GeneralConfig produceGeneralConfig(final TomlEmulatorConfig tomlEmulatorConfig) {
    return tomlEmulatorConfig.getGeneralConfig();
  }

  protected SystemProperty getConfigFileLocation() {
    return this.configFileLocation;
  }

  protected void setConfigFileLocation(final SystemProperty configFileLocation) {
    this.configFileLocation = configFileLocation;
  }
}
