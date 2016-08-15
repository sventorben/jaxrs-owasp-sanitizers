package de.sven_torben.jaxrs_owasp_sanitizers.core;

import java.util.Objects;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

public class HeaderExtractor {

  private final MultivaluedMap<String, ? extends Object> headers;

  HeaderExtractor(final MultivaluedMap<String, ? extends Object> headers) {
    this.headers = Objects.requireNonNull(headers);
  }

  MediaType extractContentTypeHeader() {
    Object contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
    if (contentType == null) {
      return null;
    }
    return MediaType.valueOf(contentType.toString());
  }
}
