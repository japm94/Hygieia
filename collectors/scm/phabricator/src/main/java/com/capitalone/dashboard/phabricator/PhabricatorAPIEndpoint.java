package com.capitalone.dashboard.phabricator;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.capitalone.dashboard.collector.GitSettings;

public class PhabricatorAPIEndpoint {

    private static final Log LOG = LogFactory.getLog(PhabricatorAPIEndpoint.class);

    private GitSettings settings;

    private static final String DEFAULT_PROTOCOL = "https";
    private static final String SEGMENT_API = "api";
    private static final String COMMITS_API = "/diffusion.querycommits";
    private static final String REPOSITORY_API = "/diffusion.repository.search";
    private static final String DETAILSCOMMIT_API = "/diffusion.commit.search";
    private static final String PARENTSCOMMIT_API = "/diffusion.commitparentsquery";
    private static final String PHABRICATOR_HOST_NAME = "pb-dc.alm-latam.accenture.com";

    // package for junit
    @SuppressWarnings({"PMD.NPathComplexity"})
    /*package
     */
    public URI buildRepoUrl() throws URISyntaxException {

        String protocol = getProtocol();
        String host = getRepoHost();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        URI uri = builder.scheme(protocol)
                .host(host)
                .pathSegment(SEGMENT_API)
                .path(REPOSITORY_API)
                .build(true).toUri();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Rest Url: " + uri);
        }

        return uri;
    }

    public URI buildCommitUrl() throws URISyntaxException {

        String protocol = getProtocol();
        String host = getRepoHost();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        URI uri = builder.scheme(protocol)
                .host(host)
                .pathSegment(SEGMENT_API)
                .path(COMMITS_API)
                .build(true).toUri();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Rest Url: " + uri);
        }

        return uri;
    }

    public URI buildParentURL() throws URISyntaxException {

        String protocol = getProtocol();
        String host = getRepoHost();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        URI uri = builder.scheme(protocol)
                .host(host)
                .pathSegment(SEGMENT_API)
                .path(PARENTSCOMMIT_API)
                .build(true).toUri();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Rest Url: " + uri);
        }

        return uri;
    }

    public URI buildCommitDetailUrl() throws URISyntaxException {

        String protocol = getProtocol();
        String host = getRepoHost();

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        URI uri = builder.scheme(protocol)
                .host(host)
                .pathSegment(SEGMENT_API)
                .path(DETAILSCOMMIT_API)
                .build(true).toUri();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Rest Url: " + uri);
        }

        return uri;
    }

    private String getProtocol() {
        return StringUtils.isBlank(settings.getProtocol()) ? DEFAULT_PROTOCOL : settings.getProtocol();
    }

    private String getRepoHost() {
        String providedHost = settings.getHost();
        String apiHost;
        if (StringUtils.isBlank(providedHost)) {
            apiHost = PHABRICATOR_HOST_NAME;
        } else {
            apiHost = providedHost;
        }
        return apiHost;
    }
}