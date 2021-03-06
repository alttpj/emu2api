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

package io.github.alttpj.emu2api.endpoint.ws.data;

import jakarta.websocket.Session;
import java.util.Optional;

public class SessionInfo {

  private String clientName;

  private String attachedToDeviceName;

  public SessionInfo(final Session session) {
    // pojo
    this.clientName = session.getId();
  }

  public String getClientName() {
    return this.clientName;
  }

  public void setClientName(final String clientName) {
    this.clientName = clientName;
  }

  public Optional<String> getAttachedToDeviceName() {
    return Optional.ofNullable(this.attachedToDeviceName);
  }

  public void setAttachedToDeviceName(final String attachedToDeviceName) {
    this.attachedToDeviceName = attachedToDeviceName;
  }
}
