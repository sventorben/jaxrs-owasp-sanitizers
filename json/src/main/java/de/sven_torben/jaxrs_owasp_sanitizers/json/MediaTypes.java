package de.sven_torben.jaxrs_owasp_sanitizers.json;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.core.MediaType;

class MediaTypes {

  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
  private static final String CHARSET_PARAMETER = "charset";
  private static final String JSON_SUBTYPE = "json";

  private MediaTypes() {
  }

  static boolean isJsonType(final MediaType mediaType) {
    if (mediaType != null) {
      String subtype = mediaType.getSubtype();
      return JSON_SUBTYPE.equalsIgnoreCase(subtype) || subtype.endsWith("+json");
    }
    return true;
  }

  static Charset extractCharset(final MediaType mediaType) {
    if (mediaType != null) {
      String charset = mediaType.getParameters()
          .getOrDefault(CHARSET_PARAMETER, DEFAULT_CHARSET.displayName());
      if (isCharsetSupported(charset)) {
        return Charset.forName(charset);
      }
    }
    return DEFAULT_CHARSET;
  }

  private static boolean isCharsetSupported(final String name) {
    try {
      Charset.forName(name);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
