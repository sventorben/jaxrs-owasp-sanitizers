package de.sven_torben.jaxrs_owasp_sanitizers.json;

import com.google.json.JsonSanitizer;

import de.sven_torben.jaxrs_owasp_sanitizers.core.MediaTypes;
import de.sven_torben.jaxrs_owasp_sanitizers.core.SanitizingReaderInterceptor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;


@Provider
public class JsonSanitizingReaderInterceptor extends SanitizingReaderInterceptor {

  public JsonSanitizingReaderInterceptor() {
    super(JsonSanitizer::sanitize);
  }

  @Override
  protected final boolean supports(MediaType contentType) {
    return contentType == null || MediaTypes.isJsonType(contentType);
  }

}
