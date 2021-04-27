module io.github.alttpj.emu2api.source.retroarch {
  requires static jakarta.cdi;
  requires static jakarta.inject;
  requires java.logging;
  requires io.github.alttpj.emu2api.event.api;
  requires io.github.alttpj.emu2api.config.base;
  requires io.github.alttpj.emu2api.source.api;
  requires io.github.alttpj.emu2api.utils.ulid;
}
