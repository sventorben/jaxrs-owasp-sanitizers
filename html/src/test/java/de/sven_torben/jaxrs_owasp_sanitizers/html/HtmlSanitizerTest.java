package de.sven_torben.jaxrs_owasp_sanitizers.html;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RunWith(Parameterized.class)
public class HtmlSanitizerTest extends JerseyTest {

  private static final String INPUT = "<b>hello <bogus></bogus><i>world</i></b>";
  private static final String SANITIZED = "<b>hello <i>world</i></b>";


  @Parameterized.Parameter(0)
  public String contentType;

  @Parameterized.Parameter(1)
  public int contentLength;

  @Parameterized.Parameter(2)
  public String expectedPayload;

  @Parameterized.Parameters
  public static Iterable<Object[]> pathsToTest() {
    return Arrays.asList(
        new Object[]{MediaType.TEXT_HTML, 25, SANITIZED},
        new Object[]{MediaType.TEXT_PLAIN, 40, INPUT}
    );
  }

  @Path("test")
  public static class TestResource {

    @GET
    @Path("html")
    @Produces({ MediaType.TEXT_HTML, MediaType.TEXT_PLAIN })
    public String writeHtml() {
      return INPUT;
    }

    @POST
    @Path("html")
    @Consumes({ MediaType.TEXT_HTML, MediaType.TEXT_PLAIN })
    public Response readHtml(
        String html, @HeaderParam(HttpHeaders.CONTENT_TYPE) MediaType contentType) {
      if (MediaType.TEXT_HTML_TYPE.isCompatible(contentType)) {
        assertThat(html, is(equalTo(SANITIZED)));
      } else {
        assertThat(html, is(equalTo(INPUT)));
      }
      return Response.ok().build();
    }

  }

  @Override
  protected Application configure() {
    return new ResourceConfig(TestResource.class)
        .register(HtmlSanitizingWriterInterceptor.class)
        .register(HtmlSanitizingReaderInterceptor.class);
  }

  @Test
  public void testWriter() {
    Response response = target("/test/html").request(contentType).get(Response.class);
    String html = response.readEntity(String.class);
    assertThat(html, is(equalTo(expectedPayload)));
    assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(equalTo(contentType)));
    if (MediaType.TEXT_HTML_TYPE.isCompatible(MediaType.valueOf(contentType))) {
      assertThat(response.getHeaderString(HttpHeaders.CONTENT_LENGTH),
          is(equalTo(String.valueOf(contentLength))));
    } else {
      assertThat(response.getHeaderString(HttpHeaders.CONTENT_LENGTH), is(nullValue()));
    }
  }

  @Test
  public void testReader() {
    Response response = target("/test/html").request().post(Entity.entity(INPUT, contentType));
    assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
  }

}
