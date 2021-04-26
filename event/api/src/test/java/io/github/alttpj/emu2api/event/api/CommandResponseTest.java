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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CommandResponseTest {

    @Test
    @DisplayName("can be hold an exception and is not successful")
    public void holdsExceptionWhenFailed() {
        // setup:
        final var responseEvent = CommandResponse.builder()
            .requestId(RequestId.create())
            .commandType(CommandType.DEVICE_LIST)
            .failedWith(Optional.of(new UnsupportedOperationException("not implemented")))
            .build();

        //expect:
        assertFalse(responseEvent.isSuccessful());

        final var failedWith = responseEvent.getFailedWith().get();
        assertTrue(failedWith instanceof UnsupportedOperationException);
        assertEquals("not implemented", failedWith.getMessage());
    }

}
