module io.github.alttpj.emu2api.endpoint.ws {
  requires static jakarta.inject;
  requires /* non-static */ jakarta.cdi;
  requires jakarta.websocket;
  requires jakarta.json.bind;
  requires jakarta.servlet;
  requires java.logging;
  requires io.github.alttpj.emu2api.event.api;

  exports io.github.alttpj.emu2api.endpoint.ws;
  exports io.github.alttpj.emu2api.endpoint.ws.json;
  exports io.github.alttpj.emu2api.endpoint.ws.data;
}
