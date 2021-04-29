module io.github.alttpj.emu2api.event.dispatcher {
  requires static jakarta.inject;
  requires static jakarta.cdi;
  requires io.github.alttpj.emu2api.source.api;
  requires io.github.alttpj.emu2api.event.api;
  requires jakarta.annotation;
  requires java.logging;
  requires io.github.alttpj.emu2api.utils.async;
}
