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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.AbstractSelectionKey;
import org.junit.jupiter.api.Test;

public class GenericResponseReaderTest {

  @Test
  public void testBufferSizeMatches() throws IOException {
    final GenericResponseReader genericResponseReader = new GenericResponseReader(5);
    final DatagramChannel udpChannel = mock(DatagramChannel.class);
    when(udpChannel.isConnected()).then(args -> true);
    when(udpChannel.read(any(ByteBuffer.class))).then(this::readImpl);

    final SelectionKey selKey = mock(AbstractSelectionKey.class);
    when(selKey.channel()).then(args -> udpChannel);

    // when:
    genericResponseReader.accept(selKey);

    // then:
    assertEquals(5, genericResponseReader.getReadBufferSize());
    assertEquals(5, genericResponseReader.getReadBuffer().length);
  }

  private int readImpl(final org.mockito.invocation.InvocationOnMock args) {
    final ByteBuffer byteBuffer = (ByteBuffer) args.getArgument(0);
    byteBuffer.put(new byte[] {0, 1, 2, 3, 4});
    return 5;
  }
}
