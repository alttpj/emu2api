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

import static org.hamcrest.MatcherAssert.assertThat;

import io.github.alttpj.emu2api.source.config.base.EmulatorConfig;
import io.github.alttpj.emu2api.source.config.base.GeneralConfig;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

public class RetroArchEmulatorSourceTest {

  private RetroArchEmulatorSource retroArchEmulatorSource;

  @BeforeEach
  public void setUp() {
    this.retroArchEmulatorSource = new RetroArchEmulatorSource();
    this.retroArchEmulatorSource.setGeneralConfig(this.produceGeneralConfig());
    this.retroArchEmulatorSource.setRetroArchConfig(this.produceEmulatorConfig());
  }

  public EmulatorConfig produceEmulatorConfig() {
    final Yaml yaml = new Yaml();
    try (final var yamlIs = openYamlConfig()) {
      final Map<String, Object> yamlConfig = yaml.load(yamlIs);

      final Map<String, Object> emulators =
          (Map<String, Object>) yamlConfig.getOrDefault("emulators", Map.of());
      final Map<String, Object> retroArch =
          (Map<String, Object>) emulators.getOrDefault("RetroArch", Map.of());

      return new SimpleRetroArchEmulatorConfig(retroArch);
    } catch (final IOException ioException) {
      throw new IllegalStateException("test defect", ioException);
    }
  }

  public GeneralConfig produceGeneralConfig() {
    return new GeneralConfig() {
      @Override
      public boolean isDebug() {
        return true;
      }

      @Override
      public Optional<Path> getLogfile() {
        return Optional.empty();
      }
    };
  }

  @Test
  public void testCustomConfig() {
    final EmulatorConfig retroArchConfig = this.retroArchEmulatorSource.getRetroArchConfig();

    final Map<String, Object> configurationMap = retroArchConfig.getConfigurationMap();

    assertThat(configurationMap, Matchers.hasKey("instances"));
    final List<Map<String, Object>> instances =
        (List<Map<String, Object>>) configurationMap.get("instances");
    assertThat(instances, Matchers.hasSize(2));
  }

  private static InputStream openYamlConfig() {
    return RetroArchEmulatorSourceTest.class.getResourceAsStream(
        "/io/github/alttpj/emu2api/source/retroarch/test-config.yaml");
  }

  /**
   * A simple config object which is testable.
   */
  private static class SimpleRetroArchEmulatorConfig implements EmulatorConfig {

    private final Map<String, Object> configMap = new ConcurrentHashMap<>();

    public SimpleRetroArchEmulatorConfig(final Map<String, Object> emulatorConfig) {
      this.configMap.putAll(emulatorConfig);
    }

    @Override
    public boolean isEnabled() {
      return true;
    }

    @Override
    public Map<String, Object> getConfigurationMap() {
      return this.configMap;
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", SimpleRetroArchEmulatorConfig.class.getSimpleName() + "[", "]")
          .add("super=" + super.toString())
          .add("configMap=" + this.configMap)
          .toString();
    }
  }
}
