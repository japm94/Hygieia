package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.phabricator.PhabricatorRestCall;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.IOUtils;
import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;


public class PhabricatorRestCallTest {

    PhabricatorRestCall rest = new PhabricatorRestCall();

    private final String repoUrl = "https://pb-dc.alm-latam.accenture.com/source/pbrepotest.git";

    private final String repoURI = "https://pb-dc.alm-latam.accenture.com/api/diffusion.repository.search";
    private final String commitURI = "https://pb-dc.alm-latam.accenture.com/api/diffusion.commit.search";
    private final String commitdetailURI = "https://pb-dc.alm-latam.accenture.com/api/diffusion.querycommits";
    private final String commitParentsURI = "https://pb-dc.alm-latam.accenture.com/api/diffusion.commitparentsquery";
    private final String repoPHID = "PHID-REPO-hkhdlgvdfofe5u3rfdqm";
    private final String commitPHID = "PHID-CMIT-tcj6u3qoi3dqklnj3q4s";
    private final String apiToken = "api-bmoeekxkmv6cfo6ry6jwjr2bbolw";
    private final String commitIdentif = "3f83c3a0f1acdc3f1aefbd8748920f3c7c5bee3a";
    private final String repoCallsign = "PBREPOTEST";

    @Test
    public void repoRestCallTest() throws IOException, UnirestException, ParseException {
        String repoJson = getJson("/phabricator/repo.json");
        JSONObject response = rest.repoRestCall(repoURI, apiToken, repoUrl);
        String json = response.toString();
        Assert.assertEquals(repoJson, json);
    }

    @Test
    public void commitRestCallTest() throws IOException, UnirestException {
        String commitJson = getJson("/phabricator/commit.json");
        JSONArray response = rest.commitRestCall(commitURI, apiToken, repoPHID);
        String json = response.toString();
        Assert.assertEquals(commitJson, json);
    }

    @Test
    public void commitDetailRestCallTest() throws IOException, UnirestException {
        String commitDetailJson = getJson("/phabricator/commitdetail.json");
        JSONObject response = rest.commitDetailRestCall(commitdetailURI, apiToken, commitPHID);
        String json = response.toString();
        Assert.assertEquals(commitDetailJson, json);

    }

    @Test
    public void commitParentsRestCallTest() throws IOException, UnirestException {
        String commitParentsJson = getJson("/phabricator/commitparents.json");
        JSONArray response = rest.commitParentsRestCall(commitParentsURI, apiToken, commitIdentif, repoCallsign);
        String json = response.toString();
        Assert.assertEquals(commitParentsJson, json);

    }

    private String getJson(String fileName) throws IOException {
        InputStream inputStream = PhabricatorRestCallTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}