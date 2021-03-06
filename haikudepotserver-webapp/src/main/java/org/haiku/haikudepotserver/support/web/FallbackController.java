/*
 * Copyright 2015-2016, Andrew Lindesay
 * Distributed under the terms of the MIT License.
 */

package org.haiku.haikudepotserver.support.web;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.haiku.haikudepotserver.dataobjects.Pkg;
import org.haiku.haikudepotserver.dataobjects.PkgVersion;
import org.haiku.haikudepotserver.dataobjects.Repository;
import org.haiku.haikudepotserver.pkg.model.PkgService;
import org.haiku.haikudepotserver.repository.model.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 * <p>If somebody makes a query such as "/apr" then the system should search for that as a package
 * and navigate to the user interface for it.</p>
 */

@Controller
public class FallbackController {

    private enum FallbackType {
        FAVICON,
        APPLETOUCH,
        PKG
    }

    protected static Logger LOGGER = LoggerFactory.getLogger(FallbackController.class);

    private final static String KEY_TERM = "term";

    private final static String VALUE_FAVICON = "favicon";

    @Resource
    private ServerRuntime serverRuntime;

    @Resource
    private PkgService pkgService;

    @Resource
    private RepositoryService repositoryService;

    @Value("${baseurl}")
    private String baseUrl;

    @Autowired(required = false) // so that integration tests run ok
    private ServletContext servletContext;

    private String termDebug(String term) {
        if(term.length() > 64) {
            return term.substring(0,64) + "...";
        }

        return term;
    }

    private void redirectToPkg(HttpServletResponse response, String term) throws IOException {
        ObjectContext context = serverRuntime.getContext();

        Optional<PkgVersion> pkgVersionOptional = tryGetPkgVersion(context, term);

        if (pkgVersionOptional.isPresent()) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).pathSegment("#!", "pkg");
            pkgVersionOptional.get().appendPathSegments(builder);
            UriComponents uriComponents = builder.build();

            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader(HttpHeaders.LOCATION, uriComponents.toUriString());

            PrintWriter w = response.getWriter();
            w.format("redirecting to; %s", uriComponents.toUriString());
            w.flush();

            LOGGER.info("did redirect to a package for; {}", term);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            PrintWriter w = response.getWriter();
            w.format("unable to find an entity for; %s", termDebug(term));
            w.flush();

            LOGGER.info("did not find a package for; {}", termDebug(term));
        }
    }

    private void streamFavicon(RequestMethod method, HttpServletResponse response) throws IOException {
        Preconditions.checkState(null!=servletContext, "the servlet context must be supplied");
        response.setContentType(com.google.common.net.MediaType.ICO.toString());

        try (InputStream inputStream = servletContext.getResourceAsStream("/img/favicon.ico")) {
            if(null==inputStream) {
                throw new IllegalStateException("unable to find the favicon resource");
            }

            if(method != RequestMethod.HEAD) {
                ByteStreams.copy(inputStream, response.getOutputStream());
            }
        }
    }

    private FallbackType getFallbackType(String term) {
        if(term.equals(VALUE_FAVICON)) {
            return FallbackType.FAVICON;
        }

        if(term.startsWith("apple-touch-icon")) {
            return FallbackType.APPLETOUCH;
        }

        return FallbackType.PKG;
    }

    private Optional<PkgVersion> tryGetPkgVersion(ObjectContext context, String term) {
        if(!Strings.isNullOrEmpty(term)) {
            Optional<Pkg> pkgOptional = Pkg.tryGetByName(context, term);

            if(pkgOptional.isPresent()) {
                List<Repository> repositories = repositoryService.getRepositoriesForPkg(context, pkgOptional.get());

                if(!repositories.isEmpty()) {
                    return pkgService.getLatestPkgVersionForPkg(
                            context,
                            pkgOptional.get(),
                            repositories.get(0));
                }
            }
        }

        return Optional.empty();
    }


    @RequestMapping(value = "/{"+KEY_TERM+"}", method = { RequestMethod.GET, RequestMethod.HEAD } )
    public void fallback(
            RequestMethod method,
            HttpServletResponse response,
            @PathVariable(value = KEY_TERM) String term)
            throws IOException {

        switch(getFallbackType(term)) {

            case APPLETOUCH:
                LOGGER.debug("unhandled apple touch icon -> 404; {}", term);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                break;

            case FAVICON:
                streamFavicon(method, response);
                break;

            case PKG:
                redirectToPkg(response, term);
                break;

            default:
                LOGGER.error("unable to handle the fallback; {}", term);

        }

    }

}
