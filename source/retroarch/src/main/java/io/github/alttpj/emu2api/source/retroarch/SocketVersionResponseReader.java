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

import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketVersionResponseReader implements Consumer<SelectionKey> {

  private static final Logger LOG =
      Logger.getLogger(SocketVersionResponseReader.class.getCanonicalName());

  private boolean connected;

  @Override
  public void accept(final SelectionKey selectionKey) {
    final SelectableChannel channel = selectionKey.channel();
    if (!(channel instanceof DatagramChannel)) {
      LOG.log(Level.WARNING, "Unexpected channel type: " + channel.getClass().getCanonicalName());
      this.connected = false;
      return;
    }

    final DatagramChannel datagramChannel = (DatagramChannel) channel;
    if (!datagramChannel.isConnected()) {
      this.connected = false;
      return;
    }

    this.readResponse(datagramChannel);
  }

  protected void readResponse(final DatagramChannel datagramChannel) {
    try {
      final ByteBuffer readBuffer = ByteBuffer.allocate(512);
      final int read = datagramChannel.read(readBuffer);

      if (read <= 0) {
        this.connected = false;
        return;
      }

      final String version = new String(readBuffer.array(), 0, read, StandardCharsets.UTF_8);
      if (version.startsWith("1.9.") || version.startsWith("2.")) {
        this.connected = true;
        return;
      }

      LOG.log(Level.WARNING, "unknown version or unsupported version: " + version);
      this.connected = false;
    } catch (final PortUnreachableException portUnreachableException) {
      if (LOG.isLoggable(Level.FINER)) {
        LOG.log(
            Level.FINER, "Destination port unreachable. RA not running?", portUnreachableException);
      } else {
        LOG.log(Level.INFO, "Destination port unreachable. RA not running?");
      }

      this.connected = false;
    } catch (final IOException javaIoIOException) {
      LOG.log(Level.WARNING, "Unexpected read exception.", javaIoIOException);
      this.connected = false;
    }
  }

  public boolean isConnected() {
    return this.connected;
  }
}
