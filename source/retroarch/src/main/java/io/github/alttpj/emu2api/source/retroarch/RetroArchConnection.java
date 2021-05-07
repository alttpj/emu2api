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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetroArchConnection {

  public static final List<String> DEFAULT_FLAGS =
      List.of("RetroArch", "CAN'T READ ROM INFO", "NO_ROM_READ", "NO_CONTROL_CMD", "NO_FILE_CMD");

  private static final Logger LOG = Logger.getLogger(RetroArchConnection.class.getCanonicalName());

  private final RetroArchDevice device;

  private DatagramChannel channel;

  private boolean isConnected;

  private String version;

  public RetroArchConnection(final RetroArchDevice device) {
    this.device = device;
  }

  public boolean canConnect() {
    if (this.channel == null) {
      try {
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
      } catch (final IOException javaIoIOException) {
        LOG.log(Level.WARNING, "Cannot open channel to " + this.device + "!", javaIoIOException);
        return false;
      }
    }

    if (!this.channel.isConnected()) {
      this.tryConnect();
    }

    if (!this.channel.isConnected()) {
      return false;
    }

    this.isConnected = true;

    final VersionResponseReader responseReader = new VersionResponseReader();
    final byte[] versions = this.sendCommand(responseReader, "VERSION");

    if (versions != null) {
      this.version = new String(versions, StandardCharsets.UTF_8);
    } else {
      LOG.log(Level.WARNING, "empty version for device " + this.device);
      this.version = "unknown";
    }

    return responseReader.isConnected();
  }

  private void tryConnect() {
    try {
      final InetSocketAddress target =
          new InetSocketAddress(this.device.getHost(), this.device.getPort());
      this.channel.connect(target);
    } catch (final IOException connectException) {
      LOG.log(Level.WARNING, "cannot connect to " + this.device + "!", connectException);
    }
  }

  public byte[] sendCommand(final AbstractUdpResponseReader responseReader, final String command) {
    try (final Selector selector = Selector.open()) {
      this.channel.register(selector, SelectionKey.OP_READ);

      if (LOG.isLoggable(Level.FINER)) {
        LOG.log(Level.FINER, () -> String.format(Locale.ENGLISH, "<< %s", command));
      }

      final ByteBuffer commandBuffer = ByteBuffer.wrap(command.getBytes(StandardCharsets.UTF_8));
      this.channel.write(commandBuffer);

      try {
        selector.select(responseReader, 200L);
      } catch (final IOException
          | ClosedSelectorException
          | IllegalArgumentException readException) {
        LOG.log(Level.WARNING, "unable to read", readException);
      }
    } catch (final IOException connectEx) {
      LOG.log(Level.WARNING, "cannot connect to " + this.device + "!", connectEx);
    }

    return responseReader.getParsedResponse();
  }

  public void close() {
    if (this.channel != null && this.channel.isOpen()) {
      try {
        this.channel.disconnect();
        this.channel.close();
      } catch (final IOException | RuntimeException socketException) {
        LOG.log(Level.WARNING, "Unable to close socket: " + this.channel, socketException);
      } finally {
        this.channel = null;
      }
    }

    this.isConnected = false;
  }

  public RetroArchDevice getDevice() {
    return this.device;
  }

  public String getVersion() {
    return this.version;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || this.getClass() != other.getClass()) {
      return false;
    }
    final RetroArchConnection that = (RetroArchConnection) other;
    return this.device.equals(that.device);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.device);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", RetroArchConnection.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("device=" + this.device)
        .add("version=" + this.version)
        .add("channel=" + this.channel)
        .add("isConnected=" + this.isConnected)
        .toString();
  }
}
