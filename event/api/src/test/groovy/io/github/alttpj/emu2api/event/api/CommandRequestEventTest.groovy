/*
 * Copyright 2021-${year} the ALttPJ Team @ https://github.com/alttpj
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

package io.github.alttpj.emu2api.event.api

import spock.lang.Specification

class CommandRequestEventTest extends Specification {

    def "assert a new RequestId is Generated for new commands"() {
        setup:
        def command = Command.builder()
                .commandType(CommandType.VERSION)
                .build();
        def commandRequest1 = CommandRequestEvent.of(command);
        def commandRequest2 = CommandRequestEvent.of(command);

        expect:
        assert commandRequest1 != commandRequest2
        assert commandRequest1.requestId != commandRequest2.requestId
        assert commandRequest1.command == commandRequest2.command

    }
}
