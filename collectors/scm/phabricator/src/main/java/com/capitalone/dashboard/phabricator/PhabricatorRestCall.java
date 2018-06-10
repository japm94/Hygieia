package com.capitalone.dashboard.phabricator;

import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
public class PhabricatorRestCall {
    private static final Log LOG = LogFactory.getLog(PhabricatorRestCall.class);

    private final RestOperations restOperations;

    @Autowired
    public PhabricatorRestCall(Supplier<RestOperations> restOperationsSupplier) {

        this.restOperations = restOperationsSupplier.get();
    }


    public JSONArray repoRestCall(URI uri, String phabricatorToken, String repoURL) throws ParseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"},"
                + "\"constraints\": {\"uris\": [\"" + repoURL + "\"]},"
                + "\"queryKey\": \"active\"}";


        HttpEntity<String> httpEntity = new HttpEntity<>(body);
        ResponseEntity<String> response = restOperations.exchange(uri, HttpMethod.POST, httpEntity, String.class);
        JSONObject jsonParentObject = paresAsObject(response);
        JSONObject resultRepo = (JSONObject) jsonParentObject.get("result");
        JSONArray dataRepo = (JSONArray) resultRepo.get("data");
        return dataRepo;
    }

    public JSONArray commitRestCall(URI uri, String phabricatorToken, String repositoryPHID) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"},"
                + " \"constraints\":{ \"repositories\": [\"" + repositoryPHID + "\"]}}";

        HttpEntity<?> httpEntity = new HttpEntity<Object>(body);
        ResponseEntity<String> response = restOperations.exchange(uri, HttpMethod.POST, httpEntity, String.class);
        JSONObject jsonParentObject = paresAsObject(response);
        JSONObject commit = (JSONObject) jsonParentObject.get("result");
        JSONArray dataCommit = (JSONArray) commit.get("data");

        return  dataCommit;

    }

    public JSONObject commitDetailRestCall(URI uri, String phabricatorToken, String commitPHID) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"}, "
                + "\"phids\":[\"" + commitPHID + "\"]}";

        HttpEntity<?> httpEntity = new HttpEntity<Object>(body);
        ResponseEntity<String> response = restOperations.exchange(uri, HttpMethod.POST, httpEntity, String.class);
        JSONObject jsonParentObject = paresAsObject(response);
        JSONObject commitDetail = (JSONObject) jsonParentObject.get("result");
        JSONObject dataCommitDetail = (JSONObject) commitDetail.get("data");
        JSONObject phidCommit = (JSONObject) dataCommitDetail.get(commitPHID);

        return phidCommit;

    }

    public List<String> commitParentsRestCall(URI uri, String phabricatorToken, String
            commitIdentif, String repoCallsign) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"},"
                + "\"commit\": \"" + commitIdentif + "\","
                + "\"callsign\": \"" + repoCallsign + "\"}";

        HttpEntity<?> httpEntity = new HttpEntity<Object>(body);
        ResponseEntity<String> response = restOperations.exchange(uri, HttpMethod.POST, httpEntity, String.class);
        JSONObject jsonParentObject = paresAsObject(response);
        JSONArray resultCommitParents = (JSONArray) jsonParentObject.get("result");
        List<String> parents = parentsList(resultCommitParents);

        return parents;

    }

    private JSONObject paresAsObject(ResponseEntity<String> response) {
        try {
            return (JSONObject) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONObject();
    }

    private List<String> parentsList(JSONArray parents ){

        String converter = parents.toString()
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "");

        String[] items = converter.split(",");
        List<String> itemList = Arrays.asList(items);


        return itemList;
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

}