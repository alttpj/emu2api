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

package io.github.alttpj.emu2api.config.toml;

import io.github.alttpj.emu2api.source.config.base.GeneralConfig;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TomlConfigProducerTest {

  @Test
  @DisplayName("Producer produces config")
  public void testProduceConfig() {
    // setup:
    final TomlConfigProducer prod = new TomlConfigProducer();
    prod.setConfigFileLocation((String) null);

    // when:
    final TomlEmulatorConfig config = prod.produceTomlEmulatorConfig();
    final GeneralConfig genConfig = prod.produceGeneralConfig(config);

    // then:
    assert !genConfig.isDebug();
    assert genConfig.getLogfile().equals(Optional.empty());
  }

}
