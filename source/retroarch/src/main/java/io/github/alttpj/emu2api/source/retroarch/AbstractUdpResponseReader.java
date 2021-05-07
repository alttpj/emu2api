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
import java.util.Locale;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractUdpResponseReader implements Consumer<SelectionKey> {

  private static final Logger LOG =
      Logger.getLogger(AbstractUdpResponseReader.class.getCanonicalName());

  private boolean connected;

  private ByteBuffer readBuffer;

  private int readCount;

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
    this.processResponse();
  }

  protected void readResponse(final DatagramChannel datagramChannel) {
    try {
      this.readBuffer = ByteBuffer.allocate(this.getReadBufferSize());
      this.readBuffer.clear();
      this.readCount = datagramChannel.read(this.readBuffer);

      if (this.readCount <= 0) {
        this.setConnected(false);
        return;
      }

      if (LOG.isLoggable(Level.FINER)) {
        LOG.log(
            Level.FINER,
            () ->
                String.format(
                    Locale.ENGLISH,
                    ">> %s",
                    new String(this.getReadBuffer(), StandardCharsets.UTF_8).trim()));
      }
    } catch (final PortUnreachableException portUnreachableException) {
      if (LOG.isLoggable(Level.FINER)) {
        LOG.log(
            Level.FINER, "Destination port unreachable. RA not running?", portUnreachableException);
      } else {
        LOG.log(Level.INFO, "Destination port unreachable. RA not running?");
      }

      this.setConnected(false);
    } catch (final IOException ioException) {
      LOG.log(Level.WARNING, "Unexpected read exception.", ioException);
      this.setConnected(false);
    }
  }

  protected abstract int getReadBufferSize();

  protected abstract void processResponse();

  public boolean isConnected() {
    return this.connected;
  }

  public void setConnected(final boolean connected) {
    this.connected = connected;
  }

  public final byte[] getReadBuffer() {
    if (this.readBuffer == null) {
      return new byte[0];
    }

    final byte[] bytes = new byte[this.readCount];
    this.readBuffer.flip();
    this.readBuffer.get(bytes);
    return bytes;
  }

  public byte[] getParsedResponse() {
    return this.getReadBuffer();
  }
}
