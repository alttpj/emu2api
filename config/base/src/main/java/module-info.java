module io.github.alttpj.emu2api.config.base {
  exports io.github.alttpj.emu2api.source.config.base;
  requires static jakarta.cdi;
  requires static jakarta.inject;

  requires java.logging;
}
