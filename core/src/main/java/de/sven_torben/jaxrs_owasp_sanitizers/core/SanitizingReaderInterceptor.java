package de.sven_torben.jaxrs_owasp_sanitizers.core;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;


public abstract class SanitizingReaderInterceptor implements ReaderInterceptor {

  private final Sanitizer sanitizer;

  public SanitizingReaderInterceptor(final Sanitizer sanitizer) {
    this.sanitizer = Objects.requireNonNull(sanitizer);
  }

  @Override
  public final Object aroundReadFrom(final ReaderInterceptorContext readerInterceptorContext)
      throws IOException, WebApplicationException {
    MultivaluedMap<String, String> headers = readerInterceptorContext.getHeaders();
    HeaderExtractor headerExtractor = new HeaderExtractor(headers);
    MediaType contentType = headerExtractor.extractContentTypeHeader();
    if (supports(contentType)) {
      InputStream originalInputStream = readerInterceptorContext.getInputStream();
      Charset charset = MediaTypes.extractCharset(contentType);
      String dirty = IOUtils.toString(originalInputStream, charset);
      String sanitized = sanitizer.sanitize(dirty);
      byte[] bytes = sanitized.getBytes(StandardCharsets.UTF_8);
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      readerInterceptorContext.setInputStream(bais);
      adjustHeaders(headers, bytes.length, contentType);
      return readerInterceptorContext.proceed();
    }
    return readerInterceptorContext.proceed();
  }

  protected abstract boolean supports(final MediaType contentType);

  private static void adjustHeaders(
      MultivaluedMap<String, String> headers, int conentLength, MediaType contentType) {
    headers.replace(
        HttpHeaders.CONTENT_TYPE, Collections.singletonList(contentType.withCharset(
            StandardCharsets.UTF_8.displayName()).toString()));
    headers.replace(
        HttpHeaders.CONTENT_LENGTH, Collections.singletonList(String.valueOf(conentLength)));
  }

}
