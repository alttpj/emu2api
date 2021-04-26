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

package io.github.alttpj.emu2api.event.api;

import io.github.alttpj.emu2api.event.api.internal.Wrapper;
import io.github.alttpj.emu2api.utils.ulid.ULID;
import org.immutables.value.Value;

@Wrapper
@Value.Immutable(builder = false)
abstract class AbstractRequestId implements Wrapped<ULID> {

  private static final long serialVersionUID = -1015384163486684501L;

  @Override
  public abstract ULID getValue();

  public static RequestId fromString(final String ulidString) {
    return RequestId.from(ULID.fromString(ulidString));
  }

  public static RequestId create() {
    return RequestId.from(ULID.nextULID());
  }
}
