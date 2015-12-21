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

import org.eclipse.che.api.core.ApiException;
import org.eclipse.che.api.core.rest.HttpJsonRequestFactory;
import org.eclipse.che.api.factory.server.FactoryService;
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
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

import static javax.ws.rs.core.UriBuilder.fromUri;

/**
 * Retrieves factory from storage via api and stores in request attribute.
 *
 * @author Max Shaposhnik
 *
 */
@Singleton
public class FactoryRetrieverFilter implements Filter {

    @Inject
    @Named("api.endpoint")
    protected String apiEndPoint;

    @Inject
    @Named("page.creation.error")
    protected String CREATION_FAILED_PAGE;

    @Inject
    @Named("page.invalid.factory")
    protected String INVALID_FACTORY_URL_PAGE;
    
    @Inject
    HttpJsonRequestFactory httpRequestFactory;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest)req;
        Factory requestedFactory;
        try {
            if (httpReq.getParameter("id") != null) {
                String getFactoryUrl = fromUri(apiEndPoint).path(FactoryService.class)
                                                           .path(FactoryService.class, "getFactory")
                                                           .build(httpReq.getParameter("id")).toString();

                requestedFactory = httpRequestFactory.fromUrl(getFactoryUrl)
                                                     .setMethod("GET")
                                                     .request()
                                                     .asDto(Factory.class);
            } else {
                String getFactoryUrl = fromUri(apiEndPoint).path(FactoryService.class)
                                                           .path(FactoryService.class, "getFactoryByAttribute")
                                                           .build().toString();
                List<Factory> matchedFactories = httpRequestFactory.fromUrl(getFactoryUrl)
                                                                   .setMethod("GET")
                                                                   .addQueryParam("name", httpReq.getParameter("name"))
                                                                   .addQueryParam("creator.userId", httpReq.getParameter("user"))
                                                                   .request()
                                                                   .asList(Factory.class);
                if (matchedFactories.isEmpty()) {
                    dispatchToErrorPage(httpReq, resp, INVALID_FACTORY_URL_PAGE, "We can not find factory with given name and user id.");
                    return;
                } 
                requestedFactory = matchedFactories.get(0);
            }

            req.setAttribute("factory", requestedFactory);
            filterChain.doFilter(req, resp);
        } catch (ApiException e) {
            dispatchToErrorPage(httpReq, resp, INVALID_FACTORY_URL_PAGE, e.getMessage());
        } catch (IOException e) {
            dispatchToErrorPage(httpReq, resp, CREATION_FAILED_PAGE, null);
        } finally {
            req.removeAttribute("factory");
        }
    }

    protected void dispatchToErrorPage(ServletRequest req, ServletResponse resp, String dispatchPath, String message)
            throws ServletException, IOException {
        if (message != null) {
            req.setAttribute(RequestDispatcher.ERROR_MESSAGE, message);
        }
        req.getRequestDispatcher(dispatchPath).forward(req, resp);
    }
}
