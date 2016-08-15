package de.sven_torben.jaxrs_owasp_sanitizers.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

public abstract class SanitizingWriterInterceptor implements WriterInterceptor {

  private final Sanitizer sanitizer;

  public SanitizingWriterInterceptor(final Sanitizer sanitizer) {
    this.sanitizer = Objects.requireNonNull(sanitizer);
  }

  @Override
  public final void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext)
      throws IOException, WebApplicationException {
    MultivaluedMap<String, Object> headers = writerInterceptorContext.getHeaders();
    HeaderExtractor headerExtractor = new HeaderExtractor(headers);
    MediaType contentType = headerExtractor.extractContentTypeHeader();

    if (supports(contentType)) {
      OutputStream originalOutputStream = writerInterceptorContext.getOutputStream();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      writerInterceptorContext.setOutputStream(baos);

      writerInterceptorContext.proceed();

      Charset charset = MediaTypes.extractCharset(contentType);
      String dirty = new String(baos.toByteArray(), charset);
      String sanitized = sanitizer.sanitize(dirty);
      byte[] bytes = sanitized.getBytes(StandardCharsets.UTF_8);
      originalOutputStream.write(bytes);
      adjustHeaders(headers, bytes.length, contentType);

    } else {
      writerInterceptorContext.proceed();
    }
  }

  protected abstract boolean supports(final MediaType contentType);

  private static void adjustHeaders(
      MultivaluedMap<String, Object> headers, int conentLength, MediaType contentType) {
    headers.replace(HttpHeaders.CONTENT_TYPE, Collections.singletonList(
        contentType.withCharset(StandardCharsets.UTF_8.displayName()).toString()));
    headers.replace(HttpHeaders.CONTENT_LENGTH, Collections.singletonList(conentLength));
  }

}
