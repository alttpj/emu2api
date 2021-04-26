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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

public class RetroArchDeviceFactoryTest {

  @Test
  public void testBuildDefault() {
    // given
    final RetroArchDeviceFactory retroArchDeviceFactory = new RetroArchDeviceFactory();

    // when
    final Map<String, Object> defaultConfigurationMap = retroArchDeviceFactory
        .getDefaultConfigurationMap();

    // then
    assertEquals("localhost", defaultConfigurationMap.get("name"));
    assertEquals("localhost", defaultConfigurationMap.get("host"));
    assertEquals(55355, defaultConfigurationMap.get("port"));
  }

}
