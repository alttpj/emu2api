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

class CommandResponseTest extends Specification {

    def 'can be hold an exception and is not successful'() {
        setup:
        def responseEvent = CommandResponse.builder()
            .requestId(RequestId.create())
            .commandType(CommandType.DEVICE_LIST)
            .failedWith(Optional.of(new UnsupportedOperationException("not implemented")))
            .build()

        expect:
        assert !responseEvent.successful

        def failedWith = responseEvent.failedWith.get()
        assert failedWith instanceof UnsupportedOperationException
        assert failedWith.message == "not implemented"
    }

}
