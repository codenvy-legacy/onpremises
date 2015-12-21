/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.onpremises.factory.filter;

import org.eclipse.che.api.factory.shared.dto.Factory;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.user.User;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.UriBuilder.fromUri;

/**
 * Check contribution workflow and redirects to github auth page if necessary.
 *
 * @author Max Shaposhnik
 *
 */
@Singleton
public class ContributionFlowFilter implements Filter {


    private static final String CONTRIBUTE_ATTRIBUTE   = "contribute";
    private static final String GITHUB_CONTRIBUTE_FLAG = "github";

    @Inject
    @Named("page.creation.error")
    protected String CREATION_FAILED_PAGE;

    @Inject
    @Named("page.invalid.factory")
    protected String INVALID_FACTORY_URL_PAGE;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }


    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
            throws IOException, ServletException {

//        HttpServletRequest httpReq = (HttpServletRequest)req;
//        Factory requestedFactory = (Factory)req.getAttribute("factory");
//        if (requestedFactory == null) {
//            dispatchToErrorPage(req, resp, CREATION_FAILED_PAGE, "Unable to get factory from request.");
//        }
//
//        // contribute workflow: need authentication and prefer Github
//        if (requestedFactory.getProject() != null && requestedFactory.getProject().getAttributes() != null) {
//
//            Map<String, List<String>> projectAttributes = requestedFactory.getProject().getAttributes();
//            boolean isAnonymous = (User.ANONYMOUS.equals(EnvironmentContext.getCurrent().getUser()) ||
//                                   EnvironmentContext.getCurrent().getUser().isTemporary());
//
//            if (isAnonymous && projectAttributes.containsKey(CONTRIBUTE_ATTRIBUTE) &&
//                GITHUB_CONTRIBUTE_FLAG.equalsIgnoreCase(projectAttributes.get(CONTRIBUTE_ATTRIBUTE).get(0))) {
//
//                URI currentUrl;
//                try {
//                    currentUrl = fromUri(httpReq.getRequestURL().toString()).replaceQuery(httpReq.getQueryString()).build();
//                } catch (IllegalArgumentException | UriBuilderException e) {
//                    dispatchToErrorPage(req, resp, INVALID_FACTORY_URL_PAGE,
//                                        "Cannot parse current URL. Please contact support for more information.");
//                    return;
//                }
//
//                String oauthRedirectAfterLoginURL = UriBuilder.fromPath("/api/oauth")
//                                                              .queryParam("redirect_url", URLEncoder.encode(currentUrl.toString(), "UTF-8"))
//                                                              .queryParam("oauth_provider", "github")
//                                                              .build().toString();
//                String oauthAuthenticateURL = UriBuilder.fromPath("/api/oauth/authenticate")
//                                                        .queryParam("oauth_provider", "github")
//                                                        .queryParam("mode", "federated_login")
//                                                        .queryParam("scope", "user,repo,write:public_key")
//                                                        .queryParam("redirect_after_login",
//                                                                    URLEncoder.encode(oauthRedirectAfterLoginURL, "UTF-8"))
//                                                        .build().toString();
//                ((HttpServletResponse)resp).sendRedirect(oauthAuthenticateURL);
//                return;
//            }
//        }
        filterChain.doFilter(req, resp);

    }

    protected void dispatchToErrorPage(ServletRequest req, ServletResponse resp, String dispatchPath, String message)
            throws ServletException, IOException {
        if (message != null) {
            req.setAttribute(RequestDispatcher.ERROR_MESSAGE, message);
        }
        req.getRequestDispatcher(dispatchPath).forward(req, resp);
    }
}
