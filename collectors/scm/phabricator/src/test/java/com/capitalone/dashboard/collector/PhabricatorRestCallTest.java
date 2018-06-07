package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.phabricator.PhabricatorRestCall;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import static org.mockito.Mockito.when;

@Component
public class PhabricatorRestCallTest {
    @Mock
    private GitSettings settings;

    @Mock
    private PhabricatorRestCall restCall;

    @Test
    public void testGetCommits() throws IOException {
        // Note that there always is paging even if results only take 1 page
        String jsonResponse1 = getJson("repo.json");
        String jsonResponse2 = getJson("commit.json");
        String jsonResponse3 = getJson("commitdetail.json");
        String jsonResponse4 = getJson("commitparents.json");

        String apiToken = "";

        URI repoURI = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.repository.search");
        String repoURL = "https://pb-dc.alm-latam.accenture.com/source/pbrepotest.git";


        URI commitURI = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.commit.search");
        String repoPHID = "PHID-REPO-hkhdlgvdfofe5u3rfdqm";

        URI commitdetailURI = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.querycommits");
        String commitPHID = "PHID-CMIT-tcj6u3qoi3dqklnj3q4s";

        String commitParentsURI = "https://pb-dc.alm-latam.accenture.com/api/diffusion.commitparentsquery";
        String commitIdentif = "3f83c3a0f1acdc3f1aefbd8748920f3c7c5bee3a";
        String repoCallsign = "PBREPOTEST";


    }

    private String getJson(String fileName) throws IOException {
        InputStream inputStream = PhabricatorRestCallTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}