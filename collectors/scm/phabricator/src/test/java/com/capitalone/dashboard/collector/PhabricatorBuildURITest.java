package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.phabricator.PhabricatorBuildURI;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;

@Component
public class PhabricatorBuildURITest {

    private static final String DEFAULT_PROTOCOL = "https";
    private static final String SEGMENT_API = "api";
    private static final String COMMITS_API = "/diffusion.commit.search";
    private static final String REPOSITORY_API = "/diffusion.repository.search";
    private static final String PHABRICATOR_HOST_NAME = "pb-dc.alm-latam.accenture.com";

    PhabricatorBuildURI url;

    @Test
    public void testBuildUri() throws URISyntaxException {

        String repoURL = "https://pb-dc.alm-latam.accenture.com/api/diffusion.repository.search";
        String commitURL = "https://pb-dc.alm-latam.accenture.com/api/diffusion.commit.search";

        URI res;

        res = buildRepoUrl();
        assertEquals(repoURL, res.toString());

        res = buildCommitUrl();
        assertEquals(commitURL, res.toString());

    }

    public URI buildRepoUrl() throws URISyntaxException {

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        URI uri = builder.scheme(DEFAULT_PROTOCOL)
                .host(PHABRICATOR_HOST_NAME)
                .pathSegment(SEGMENT_API)
                .path(REPOSITORY_API)
                .build(true).toUri();

        return uri;
    }

    public URI buildCommitUrl() throws URISyntaxException {

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();

        URI uri = builder.scheme(DEFAULT_PROTOCOL)
                .host(PHABRICATOR_HOST_NAME)
                .pathSegment(SEGMENT_API)
                .path(COMMITS_API)
                .build(true).toUri();

        return uri;
    }

}