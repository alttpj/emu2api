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

package io.github.alttpj.emu2api.server.meecrowave;

import org.apache.meecrowave.runner.Cli;

public final class MeecrowaveServer {

  private MeecrowaveServer() {
    // utility class
  }

  public static void main(final String[] args) {
    // System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    Cli.main(new String[] {"--logging-global-setup", "true", "--log4j2-jul-bridge", "true"});
  }
}
