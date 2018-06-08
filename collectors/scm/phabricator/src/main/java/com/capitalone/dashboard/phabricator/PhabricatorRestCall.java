package com.capitalone.dashboard.phabricator;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class PhabricatorRestCall {
    private static final Log LOG = LogFactory.getLog(PhabricatorRestCall.class);


    public JSONObject repoRestCall(String uri, String phabricatorToken, String repoURL) throws UnirestException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        JsonNode response = Unirest.post(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("params={\"__conduit__\":{\"token\":\""+phabricatorToken+"\"}," +
                        "\"constraints\": {\"uris\": [\""+repoURL+"\"]}," +
                        "\"queryKey\": \"active\"}").asJson().getBody();

        JSONObject repoResult = response.getObject().getJSONObject("result");
        JSONArray repoData = repoResult.getJSONArray("data");
        String repoPHID = null;
        String repoCallsign = null;

        for (int i = 0; i< repoData.length(); i++){
            JSONObject obj = repoData.getJSONObject(i);
            repoPHID = obj.getString("phid");
            JSONObject fields = (JSONObject) obj.get("fields");
            repoCallsign = fields.getString("callsign");
        }

        String values = "{ \"repoPHID\":\""+repoPHID+"\", \"callsign\":\""+repoCallsign+"\"}";
        JSONObject repoJSON = new JSONObject(values);
        return repoJSON;

    }

    public JSONArray commitRestCall(String uri, String phabricatorToken, String repositoryPHID) throws UnirestException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        JsonNode response = Unirest.post(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"},"
                        + " \"constraints\":{ \"repositories\": [\"" + repositoryPHID + "\"]}}").asJson().getBody();

        JSONObject commitResult = response.getObject().getJSONObject("result");
        JSONArray commitData = (JSONArray) commitResult.get("data");

        return commitData;

    }

    public JSONObject commitDetailRestCall(String uri, String phabricatorToken, String commitPHID) throws UnirestException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        JsonNode response = Unirest.post(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"}, "
                        + "\"phids\":[\"" + commitPHID + "\"]}").asJson().getBody();

        JSONObject repoResult = response.getObject().getJSONObject("result");
        JSONObject commitDetail= (JSONObject) repoResult.get("data");


        return commitDetail;

    }

    public JSONArray commitParentsRestCall(String uri, String phabricatorToken, String
            commitIdentif, String repoCallsign) throws UnirestException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        JsonNode response = Unirest.post(uri)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body("params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"},"
                        + "\"commit\": \"" + commitIdentif + "\","
                        + "\"callsign\": \"" + repoCallsign + "\"}").asJson().getBody();

        JSONArray commitParents = response.getArray();

        return commitParents;

    }

}