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

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VersionResponseReader extends AbstractUdpResponseReader {

  private static final Logger LOG =
      Logger.getLogger(VersionResponseReader.class.getCanonicalName());

  private String version;

  @Override
  protected void processResponse() {
    this.version = new String(this.getReadBuffer(), StandardCharsets.UTF_8);
    if (this.version.startsWith("1.9.") || this.version.startsWith("2.")) {
      this.setConnected(true);
      return;
    }

    LOG.log(Level.WARNING, "unknown version or unsupported version: " + this.version);
    this.setConnected(false);
  }

  @Override
  protected int getReadBufferSize() {
    return 64;
  }

  public Optional<String> getVersion() {
    return Optional.ofNullable(this.version);
  }
}
