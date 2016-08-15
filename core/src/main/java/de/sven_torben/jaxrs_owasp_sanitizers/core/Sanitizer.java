package de.sven_torben.jaxrs_owasp_sanitizers.core;

@FunctionalInterface
public interface Sanitizer {
  String sanitize(final String dirty);
}
