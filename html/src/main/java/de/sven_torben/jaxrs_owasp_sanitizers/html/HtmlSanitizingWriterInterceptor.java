package de.sven_torben.jaxrs_owasp_sanitizers.html;

import static de.sven_torben.jaxrs_owasp_sanitizers.html.HtmlSanitizingReaderInterceptor.DEFAULT_POLICY;

import de.sven_torben.jaxrs_owasp_sanitizers.core.SanitizingWriterInterceptor;
import org.owasp.html.PolicyFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
public class HtmlSanitizingWriterInterceptor extends SanitizingWriterInterceptor {

  public HtmlSanitizingWriterInterceptor() {
    this(DEFAULT_POLICY);
  }

  public HtmlSanitizingWriterInterceptor(final PolicyFactory policyFactory) {
    super(policyFactory::sanitize);
  }

  @Override
  protected boolean supports(MediaType contentType) {
    return contentType == null || MediaType.TEXT_HTML_TYPE.isCompatible(contentType);
  }

}
