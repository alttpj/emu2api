module io.github.alttpj.emu2api.event.api {
  exports io.github.alttpj.emu2api.event.api;

  requires static org.immutables.value.annotations;
  requires static java.compiler;
  requires static jakarta.inject;
  requires /* non-static */ jakarta.cdi;
  requires io.github.alttpj.emu2api.utils.ulid;
}
