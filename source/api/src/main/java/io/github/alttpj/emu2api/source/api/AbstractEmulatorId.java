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

package io.github.alttpj.emu2api.source.api;

import io.github.alttpj.emu2api.source.api.internal.Wrapper;
import io.github.alttpj.emu2api.utils.ulid.ULID;
import org.immutables.value.Value;

@Wrapper
@Value.Immutable
public class AbstractEmulatorId implements Wrapped<ULID> {

  private static final long serialVersionUID = 4734011114500574393L;

  @Override
  public ULID getValue() {
    // TODO: implement
    throw new UnsupportedOperationException(
        "not yet implemented: [io.github.alttpj.emu2api.source.api.AbstractEmulatorId::getValue].");
  }
}