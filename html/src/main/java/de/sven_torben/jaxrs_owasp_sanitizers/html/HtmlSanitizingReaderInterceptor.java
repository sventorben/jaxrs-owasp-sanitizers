package de.sven_torben.jaxrs_owasp_sanitizers.html;

import de.sven_torben.jaxrs_owasp_sanitizers.core.SanitizingReaderInterceptor;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;


@Provider
public class HtmlSanitizingReaderInterceptor extends SanitizingReaderInterceptor {

  static final PolicyFactory DEFAULT_POLICY = Sanitizers.BLOCKS.and(Sanitizers.FORMATTING)
      .and(Sanitizers.IMAGES).and(Sanitizers.LINKS).and(Sanitizers.STYLES).and(Sanitizers.TABLES);

  public HtmlSanitizingReaderInterceptor() {
    this(DEFAULT_POLICY);
  }

  public HtmlSanitizingReaderInterceptor(final PolicyFactory policyFactory) {
    super(policyFactory::sanitize);
  }

  @Override
  protected final boolean supports(final MediaType contentType) {
    return contentType == null || MediaType.TEXT_HTML_TYPE.isCompatible(contentType);
  }

}
