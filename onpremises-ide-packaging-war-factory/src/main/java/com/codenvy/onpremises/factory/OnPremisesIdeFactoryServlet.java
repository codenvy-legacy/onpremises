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
package com.codenvy.onpremises.factory;

import org.eclipse.che.api.factory.shared.dto.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URLDecoder;

import static com.google.common.base.Strings.nullToEmpty;
import static javax.ws.rs.core.UriBuilder.fromUri;

/**
 * Servlet to handle factory URL's.
 *
 */
@Singleton
public class OnPremisesIdeFactoryServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(OnPremisesIdeFactoryServlet.class);


    private static String CREATION_FAILED_PAGE;

    @Inject
    public OnPremisesIdeFactoryServlet(@Named("page.creation.error") String creationFailedPage) {
        super();
        CREATION_FAILED_PAGE = creationFailedPage;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Factory requestedFactory = (Factory)req.getAttribute("factory");
        if (requestedFactory == null) {
            dispatchToErrorPage(req, resp, CREATION_FAILED_PAGE, "Unable to get factory from request.");
        }

//        boolean isAnonymous = (User.ANONYMOUS.equals(EnvironmentContext.getCurrent().getUser()) ||
//                                       EnvironmentContext.getCurrent().getUser().isTemporary());
//
//          //TODO: use from policy
//        String location = (factoryWorkspace != null && factoryWorkspace.getLocation() != null) ? factoryWorkspace.getLocation() : "acceptor";
//        String type = (factoryWorkspace != null && factoryWorkspace.getType() != null) ? factoryWorkspace.getType() : "named";
//
//        if (isAnonymous && !(location.equals("owner") && type.equals("temp"))) {
//            dispatchToLoginPage(req, resp);
//            return;
//        }



        // DO NOT REMOVE! This log will be used in statistic analyzing
        LOG.info("EVENT#factory-accepted# WS#{}# REFERRER#{}# FACTORY#{}# ORG-ID#{}# AFFILIATE-ID#{}# WS-LOCATION#{}# WS-TYPE#{}#",
                 requestedFactory.getWorkspace().getName(),
                 nullToEmpty(req.getHeader("Referer")),
                 URLDecoder.decode(fromUri(req.getRequestURL().toString()).replaceQuery(req.getQueryString()).build().toString(), "UTF-8"),
                 nullToEmpty(requestedFactory.getCreator() != null ? requestedFactory.getCreator().getAccountId() : null),
                 "", //AFFILIATE-ID
                 "", //TODO: use from policy
                 "" //TODO: use from policy
                );
        UriBuilder redirectAfterStateOnline = UriBuilder.fromPath("/ws").queryParam("factory", requestedFactory.getId());
        resp.sendRedirect(redirectAfterStateOnline.build().toString());
    }




    private void dispatchToErrorPage(HttpServletRequest req, HttpServletResponse resp, String dispatchPath, String message)
            throws ServletException, IOException {
        if (message != null) {
            LOG.debug(message);
            req.setAttribute(RequestDispatcher.ERROR_MESSAGE, message);
        }
        req.getRequestDispatcher(dispatchPath).forward(req, resp);
    }

    private void dispatchToLoginPage(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UriBuilder builder = fromUri((req.getRequestURL() + (req.getQueryString() != null ? "?" + req.getQueryString() : "")))
                .queryParam("login", "");
        resp.sendRedirect(builder.build().toString());
    }
}
