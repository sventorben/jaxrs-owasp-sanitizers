package de.sven_torben.jaxrs_owasp_sanitizers.json;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.core.MediaType;

@RunWith(DataProviderRunner.class)
public class MediaTypesTest {


  @DataProvider
  public static Object[][] jsonMediaTypes() {
    return new Object[][] {
        { "application/json" },
        { "text/json" },
        { "application/hal+json" },
        { "custom/vnd.example.company+json" }
    };
  }

  @DataProvider
  public static Object[][] nonJsonMediaTypes() {
    return new Object[][] {
        { "application/atom+xml" },
        { "text/plain" },
        { "application/hal+nonjson" },
        { "json/application" },
        { "application/json+xml" }
    };
  }

  @DataProvider
  public static Object[][] mediatypesToCharsets() {
    return new Object[][] {
        { null, StandardCharsets.UTF_8 },
        { MediaType.valueOf("*/*"), StandardCharsets.UTF_8 },
        { MediaType.valueOf("*/*;charset=US-ASCII"), StandardCharsets.US_ASCII },
        { MediaType.valueOf("*/*;charset=UNSUPPORTED"), StandardCharsets.UTF_8 },
        { MediaType.valueOf("*/*;charset=US-ASCII;encoding=UTF-8"), StandardCharsets.US_ASCII }
    };
  }

  @Test
  @UseDataProvider("jsonMediaTypes")
  public void testJsonMediaTypes(String mediaType) {
    assertThat(MediaTypes.isJsonType(MediaType.valueOf(mediaType)), is(true));
  }

  @Test
  @UseDataProvider("nonJsonMediaTypes")
  public void testNonJsonMediaTypes(String mediaType) {
    assertThat(MediaTypes.isJsonType(MediaType.valueOf(mediaType)), is(false));
  }

  @Test
  public void testNonJsonMediaTypes() {
    assertThat(MediaTypes.isJsonType(null), is(true));
  }

  @Test
  @UseDataProvider("mediatypesToCharsets")
  public void testCharsets(MediaType input, Charset expected) {
    assertThat(MediaTypes.extractCharset(input), is(equalTo(expected)));
  }

}