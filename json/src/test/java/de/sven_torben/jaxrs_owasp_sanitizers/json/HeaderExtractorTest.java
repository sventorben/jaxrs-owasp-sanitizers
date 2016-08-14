package de.sven_torben.jaxrs_owasp_sanitizers.json;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class HeaderExtractorTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private MultivaluedMap<String, Object> headers;
  private HeaderExtractor cut;

  @Before
  public void setUp() {
    headers = new MultivaluedHashMap<>();
    cut = new HeaderExtractor(headers);
  }

  @Test
  public void headersMustBePresent() {
    thrown.expect(NullPointerException.class);
    new HeaderExtractor(null);
  }

  @Test
  public void testNoContentTypeHeader() {
    MediaType contentType = cut.extractContentTypeHeader();
    assertThat(contentType, is(nullValue()));
  }

  @Test
  public void testSingleContentTypeHeaderAsString() {
    headers.putSingle(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    MediaType contentType = cut.extractContentTypeHeader();
    assertThat(contentType, is(MediaType.APPLICATION_JSON_TYPE));
  }

  @Test
  public void testSingleContentTypeHeaderAsMediaType() {
    headers.putSingle(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE);
    MediaType contentType = cut.extractContentTypeHeader();
    assertThat(contentType, is(MediaType.APPLICATION_JSON_TYPE));
  }

  @Test
  public void testMultipleContentTypeHeaders() {
    headers.put(HttpHeaders.CONTENT_TYPE,
        Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
    MediaType contentType = cut.extractContentTypeHeader();
    assertThat(contentType, is(MediaType.APPLICATION_JSON_TYPE));
  }

  @Test
  public void testInvalidContentType() {
    thrown.expect(IllegalArgumentException.class);
    headers.putSingle(HttpHeaders.CONTENT_TYPE, "foo/bar/buzz");
    cut.extractContentTypeHeader();
  }

}