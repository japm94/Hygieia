package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.phabricator.PhabricatorBuildURI;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;

public class PhabricatorBuildURITest {

    @Test
    public void testBuildUri() throws URISyntaxException {

        PhabricatorBuildURI url = new PhabricatorBuildURI();

        URI repoURL = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.repository.search");
        URI commitURL = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.commit.search");
        URI commitDetailURL = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.querycommits");
        URI commitParents = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.commitparentsquery");


        URI res;
        res = url.buildRepoUrl();
        assertEquals(repoURL, res);

        res = url.buildCommitUrl();
        assertEquals(commitURL, res);

        res = url.buildCommitDetailUrl();
        assertEquals(commitDetailURL, res);

        res = url.buildParentURL();
        assertEquals(commitParents, res);

    }

}