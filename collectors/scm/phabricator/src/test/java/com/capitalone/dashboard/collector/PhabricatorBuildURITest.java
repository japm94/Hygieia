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

        String repoURL = "https://pb-dc.alm-latam.accenture.com/api/diffusion.repository.search";
        String commitURL = "https://pb-dc.alm-latam.accenture.com/api/diffusion.commit.search";
        String commitDetailURL = "https://pb-dc.alm-latam.accenture.com/api/diffusion.querycommits";
        String commitParents = "https://pb-dc.alm-latam.accenture.com/api/diffusion.commitparentsquery";


        String res;
        res = url.buildRepoUrl();
        assertEquals(repoURL, res.toString());

        res = url.buildCommitUrl();
        assertEquals(commitURL, res.toString());

        res = url.buildCommitDetailUrl();
        assertEquals(commitDetailURL, res.toString());

        res = url.buildParentURL();
        assertEquals(commitParents, res.toString());

    }

}