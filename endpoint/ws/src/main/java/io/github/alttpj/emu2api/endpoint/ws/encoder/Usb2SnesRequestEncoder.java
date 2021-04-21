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

package io.github.alttpj.emu2api.endpoint.ws.encoder;

import io.github.alttpj.emu2api.endpoint.ws.data.Usb2SnesRequest;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class Usb2SnesRequestEncoder
    implements Encoder.TextStream<Usb2SnesRequest>, Decoder.TextStream<Usb2SnesRequest> {

  private static final Jsonb JSON = JsonbBuilder.create();

  @Override
  public Usb2SnesRequest decode(final Reader reader) throws DecodeException, IOException {
    try {
      return JSON.fromJson(reader, Usb2SnesRequest.class);
    } catch (final IllegalArgumentException | JsonbException jsonbEx) {
      throw new IOException(jsonbEx);
    }
  }

  @Override
  public void encode(final Usb2SnesRequest object, final Writer writer)
      throws EncodeException, IOException {
    try {
      JSON.toJson(object, writer);
    } catch (final JsonbException jsonbEx) {
      throw new IOException(jsonbEx);
    }
  }

  @Override
  public void init(final EndpointConfig config) {
    // noop
  }

  @Override
  public void destroy() {
    // noop
  }
}
