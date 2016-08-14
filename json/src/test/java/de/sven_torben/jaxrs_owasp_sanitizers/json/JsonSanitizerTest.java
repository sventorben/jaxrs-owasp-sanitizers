package de.sven_torben.jaxrs_owasp_sanitizers.json;

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
public class JsonSanitizerTest extends JerseyTest {

  private static final String CUSTOM_JSON_CONTENT_TYPE = "custom/vnd.me+json";

  private static final String INPUT = "false,true";
  private static final String SANITIZED = "false";


  @Parameterized.Parameter(0)
  public String contentType;

  @Parameterized.Parameter(1)
  public int contentLength;

  @Parameterized.Parameter(2)
  public String expectedPayload;

  @Parameterized.Parameters
  public static Iterable<Object[]> pathsToTest() {
    return Arrays.asList(
        new Object[]{MediaType.APPLICATION_JSON, 5, SANITIZED},
        new Object[]{CUSTOM_JSON_CONTENT_TYPE, 5, SANITIZED},
        new Object[]{MediaType.TEXT_PLAIN, 10, INPUT}
    );
  }

  @Path("test")
  public static class TestResource {

    @GET
    @Path("json")
    @Produces({ MediaType.APPLICATION_JSON, CUSTOM_JSON_CONTENT_TYPE, MediaType.TEXT_PLAIN })
    public String writeJson() {
      return INPUT;
    }

    @POST
    @Path("json")
    @Consumes({ MediaType.APPLICATION_JSON, CUSTOM_JSON_CONTENT_TYPE, MediaType.TEXT_PLAIN })
    public Response readJson(
        String json, @HeaderParam(HttpHeaders.CONTENT_TYPE) MediaType contentType) {
      if (MediaTypes.isJsonType(contentType)) {
        assertThat(json, is(equalTo(SANITIZED)));
      } else {
        assertThat(json, is(equalTo(INPUT)));
      }
      return Response.ok().build();
    }

  }

  @Override
  protected Application configure() {
    return new ResourceConfig(TestResource.class)
        .register(JsonSanitizingWriterInterceptor.class)
        .register(JsonSanitizingReaderInterceptor.class);
  }

  @Test
  public void testWriter() {
    Response response = target("/test/json").request(contentType).get(Response.class);
    String json = response.readEntity(String.class);
    assertThat(json, is(equalTo(expectedPayload)));
    assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE), is(equalTo(contentType)));
    if (MediaTypes.isJsonType(MediaType.valueOf(contentType))) {
      assertThat(response.getHeaderString(HttpHeaders.CONTENT_LENGTH),
          is(equalTo(String.valueOf(contentLength))));
    } else {
      assertThat(response.getHeaderString(HttpHeaders.CONTENT_LENGTH), is(nullValue()));
    }
  }

  @Test
  public void testReader() {
    Response response = target("/test/json").request().post(Entity.entity(INPUT, contentType));
    assertThat(response.getStatus(), is(equalTo(Response.Status.OK.getStatusCode())));
  }

}
