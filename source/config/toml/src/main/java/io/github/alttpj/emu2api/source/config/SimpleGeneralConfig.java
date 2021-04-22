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

package io.github.alttpj.emu2api.source.config;

import io.github.alttpj.emu2api.source.api.config.GeneralConfig;
import java.nio.file.Path;
import java.util.Optional;

class SimpleGeneralConfig implements GeneralConfig {

  private final boolean isDebug;
  private final Path logfile;

  public SimpleGeneralConfig(final boolean isDebug, final Path logFileLocation) {
    this.isDebug = isDebug;
    this.logfile = logFileLocation;
  }

  @Override
  public boolean isDebug() {
    return this.isDebug;
  }

  @Override
  public Optional<Path> getLogfile() {
    return Optional.ofNullable(this.logfile);
  }
}
