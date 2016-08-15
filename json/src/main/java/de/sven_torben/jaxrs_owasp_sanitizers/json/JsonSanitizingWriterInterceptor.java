package de.sven_torben.jaxrs_owasp_sanitizers.json;

import com.google.json.JsonSanitizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class JsonSanitizingWriterInterceptor implements WriterInterceptor {
  @Override
  public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext)
      throws IOException, WebApplicationException {
    MultivaluedMap<String, Object> headers = writerInterceptorContext.getHeaders();
    HeaderExtractor headerExtractor = new HeaderExtractor(headers);
    MediaType contentType = headerExtractor.extractContentTypeHeader();

    if (contentType == null || MediaTypes.isJsonType(contentType)) {
      OutputStream originalOutputStream = writerInterceptorContext.getOutputStream();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      writerInterceptorContext.setOutputStream(baos);

      writerInterceptorContext.proceed();

      Charset charset = MediaTypes.extractCharset(contentType);
      String jsonish = new String(baos.toByteArray(), charset);
      String sanitized = JsonSanitizer.sanitize(jsonish);
      byte[] bytes = sanitized.getBytes(StandardCharsets.UTF_8);
      originalOutputStream.write(bytes);
      adjustHeaders(headers, bytes.length, contentType);

    } else {
      writerInterceptorContext.proceed();
    }
  }

  private static void adjustHeaders(
      MultivaluedMap<String, Object> headers, int conentLength, MediaType contentType) {
    headers.replace(HttpHeaders.CONTENT_TYPE, Collections.singletonList(
        contentType.withCharset(StandardCharsets.UTF_8.displayName()).toString()));
    headers.replace(HttpHeaders.CONTENT_LENGTH, Collections.singletonList(conentLength));
  }

}
