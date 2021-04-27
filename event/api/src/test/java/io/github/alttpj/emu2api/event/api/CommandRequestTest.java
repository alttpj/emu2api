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

package io.github.alttpj.emu2api.event.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommandRequestTest {

  @Test
  @DisplayName("assert a new RequestId is Generated for new commands")
  public void testNewRequestIdPerNewCommand() {
    // setup:
    final var commandRequest1 = CommandRequest.of(CommandType.DEVICE_LIST);
    final var commandRequest2 = CommandRequest.of(CommandType.DEVICE_LIST);

    // expect:
    assertNotEquals(commandRequest1, commandRequest2);
    assertNotEquals(commandRequest1.getRequestId(), commandRequest2.getRequestId());
    assertEquals(commandRequest1.getCommandType(), commandRequest2.getCommandType());
  }
}
