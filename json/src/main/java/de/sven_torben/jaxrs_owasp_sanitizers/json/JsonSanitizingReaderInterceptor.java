package de.sven_torben.jaxrs_owasp_sanitizers.json;

import com.google.json.JsonSanitizer;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;


@Provider
public class JsonSanitizingReaderInterceptor implements ReaderInterceptor {

  @Override
  public Object aroundReadFrom(ReaderInterceptorContext readerInterceptorContext)
      throws IOException, WebApplicationException {
    MultivaluedMap<String, String> headers = readerInterceptorContext.getHeaders();
    HeaderExtractor headerExtractor = new HeaderExtractor(headers);
    MediaType contentType = headerExtractor.extractContentTypeHeader();
    if (contentType == null || MediaTypes.isJsonType(contentType)) {
      InputStream originalInputStream = readerInterceptorContext.getInputStream();
      Charset charset = MediaTypes.extractCharset(contentType);
      String jsonish = IOUtils.toString(originalInputStream, charset);
      String sanitized = JsonSanitizer.sanitize(jsonish);
      byte[] bytes = sanitized.getBytes(StandardCharsets.UTF_8);
      try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
        adjustHeaders(headers, bytes.length, contentType);
        readerInterceptorContext.setInputStream(bais);
        return readerInterceptorContext.proceed();
      }
    }
    return readerInterceptorContext.proceed();
  }

  private static void adjustHeaders(
      MultivaluedMap<String, String> headers, int conentLength, MediaType contentType) {
    headers.replace(
        HttpHeaders.CONTENT_TYPE, Collections.singletonList(contentType.withCharset(
            StandardCharsets.UTF_8.displayName()).toString()));
    headers.replace(
        HttpHeaders.CONTENT_LENGTH, Collections.singletonList(String.valueOf(conentLength)));
  }

}
