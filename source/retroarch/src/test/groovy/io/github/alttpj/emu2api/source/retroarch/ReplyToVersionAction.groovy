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

package io.github.alttpj.emu2api.source.retroarch

import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

class ReplyToVersionAction implements Consumer<SelectionKey> {

  AtomicBoolean responded = new AtomicBoolean()

  @Override
  void accept(SelectionKey selectionKey) {
    def channel = selectionKey.channel() as DatagramChannel
    def allocate = ByteBuffer.allocate(25)
    allocate.clear()

    try {
      def socketAddress = channel.receive(allocate)
      def position = allocate.position()
      def command = new String(allocate.array(), 0, position, StandardCharsets.UTF_8)

      if ('VERSION' == command) {
        def response = ByteBuffer.wrap('1.9.1'.getBytes(StandardCharsets.UTF_8))
        def send = channel.send(response, socketAddress)
        assert send == '1.9.1'.length()
        this.responded.set true
      }
    } catch (Throwable error) {
      System.out.println(error.getMessage())
    }

  }
}
