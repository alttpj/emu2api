module io.github.alttpj.emu2api.source.config.toml {
  requires static microprofile.config.api;
  requires /* non-static */ jakarta.cdi; // b/c of InjectionPoint.
  requires java.logging;
  requires io.github.alttpj.emu2api.config.base;
  requires com.fasterxml.jackson.dataformat.toml;

  opens io.github.alttpj.emu2api.config.toml to
      com.fasterxml.jackson.dataformat.toml;

  exports io.github.alttpj.emu2api.config.toml to
      com.fasterxml.jackson.dataformat.toml;
}
