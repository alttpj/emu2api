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

import org.junit.jupiter.api.Test;

public class RetroArchAddressServiceImplTest {

  RetroArchAddressServiceImpl service = new RetroArchAddressServiceImpl(true);

  @Test
  public void testOpenTrackerInput() {
    // given:
    final int asInt = Integer.parseInt("F50010", 16);

    // when:
    final int retroArchAddress = this.service.translateAddressToRetroArch(asInt);

    // then:
    assertEquals(0x7E0010, retroArchAddress);
  }

  @Test
  public void testOpenTrackerInput2() {
    // given:
    final int asInt = Integer.parseInt("F5F000", 16);

    // when:
    final int retroArchAddress = this.service.translateAddressToRetroArch(asInt);

    // then:
    assertEquals(0x7ef000, retroArchAddress);
  }
}
