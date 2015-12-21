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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Redirects user to the agreement page when using factory for first time.
 *
 * @author Max Shaposhnik
 *
 */
@Singleton
public class AgreementPageFilter implements Filter {

    @Inject
    @Named("page.creation.error")
    protected String CREATION_FAILED_PAGE;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
//        HttpServletRequest httpReq = (HttpServletRequest)servletRequest;
//        HttpServletResponse resp = (HttpServletResponse)servletResponse;
//        String method = httpReq.getMethod();
//        String factoryId = httpReq.getParameter("id");
//
//        if ("GET".equals(method) && factoryId != null) {
//
//            Cookie[] cookies = httpReq.getCookies();
//            if (cookies != null) {
//                for (Cookie cookie : cookies) {
//                    if (cookie.getName().equals(factoryId) && cookie.getValue().equals("accepted")) {
//                        filterChain.doFilter(servletRequest, servletResponse);
//                        return;
//                    }
//                }
//            }
//
//            Factory requestedFactory = (Factory)servletRequest.getAttribute("factory");
//            if (requestedFactory == null) {
//                dispatchToErrorPage(servletRequest, resp, CREATION_FAILED_PAGE, "Unable to get factory from request.");
//            }
//
//            if (requestedFactory.getWorkspace() == null || requestedFactory.getWorkspace().getLocation() == null ||
//                                                   "acceptor".equals(requestedFactory.getWorkspace().getLocation())) {
//                UriBuilder redirectUrl = UriBuilder.fromPath("/site/factory-usage-notification")
//                                                   .queryParam("redirect_url", URLEncoder.encode(
//                                                           UriBuilder.fromUri(httpReq.getRequestURL().toString())
//                                                                     .replacePath(httpReq.getContextPath())
//                                                                     .queryParam("id", factoryId).build()
//                                                                     .toString(), "UTF-8"));
//                resp.sendRedirect(redirectUrl.build().toString());
//                return;
//            }
//        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    protected void dispatchToErrorPage(ServletRequest req, ServletResponse resp, String dispatchPath, String message)
            throws ServletException, IOException {
        if (message != null) {
            req.setAttribute(RequestDispatcher.ERROR_MESSAGE, message);
        }
        req.getRequestDispatcher(dispatchPath).forward(req, resp);
    }
}
