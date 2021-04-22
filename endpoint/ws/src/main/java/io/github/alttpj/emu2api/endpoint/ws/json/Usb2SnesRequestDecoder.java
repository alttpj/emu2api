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

package io.github.alttpj.emu2api.endpoint.ws.json;

import io.github.alttpj.emu2api.endpoint.ws.data.Usb2SnesRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.json.bind.Jsonb;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Reader;

@ApplicationScoped
public class Usb2SnesRequestDecoder implements Decoder.TextStream<Usb2SnesRequest> {

  private Jsonb jsonb;

  @Override
  public Usb2SnesRequest decode(final Reader reader) throws DecodeException, IOException {
    try {
      return this.jsonb.fromJson(reader, Usb2SnesRequest.class);
    } catch (final Exception jsonbEx) {
      throw new IOException(jsonbEx);
    }
  }

  @Override
  public void init(final EndpointConfig config) {
    if (this.jsonb == null) {
      this.jsonb = CDI.current().select(Jsonb.class).get();
    }
  }

  @Override
  public void destroy() {
    // noop
  }
}
