package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.phabricator.PhabricatorAPIEndpoint;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of a git client to connect to an Phabricator <i>Server</i>.
 */

@Component("phabricator")
public class DefaultPhabricatorClient implements GitClient {
    private static final Log LOG = LogFactory.getLog(DefaultPhabricatorClient.class);

    private final GitSettings settings;

    private final RestOperations restOperations;

    private PhabricatorAPIEndpoint endpoint;

    @Autowired
    public DefaultPhabricatorClient(GitSettings settings, Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }

    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public List<Commit> getCommits(GitRepo repo, boolean firstRun) {
        List<Commit> commits = new ArrayList<>();

        try {

            // Phabricator Token
            String apiToken = settings.getToken();

            //Phabricator endpoints
            URI repoEndpoint = endpoint.buildRepoUrl();
            URI commitEndpoint = endpoint.buildCommitUrl();
            URI commitDetails = endpoint.buildCommitDetailUrl();

            ResponseEntity<String> repoAPI = repoRestCall(repoEndpoint, apiToken, repo.getRepoUrl());
            JSONObject repoJSON = paresAsObject(repoAPI);
            JSONObject repoResult = (JSONObject) repoJSON.get("result");
            JSONArray repoData = (JSONArray) repoResult.get("data");
            String repositoryPHID = null;
            if (repoData != null) {
                for (Object phid : repoData) {
                    repositoryPHID = str((JSONObject) phid, "phid");
                }
            }

            ResponseEntity<String> commitAPI = commitRestCall(commitEndpoint, apiToken, repositoryPHID);
            JSONObject jsonParentObject = paresAsObject(commitAPI);
            JSONObject resultCommitAPI = (JSONObject) jsonParentObject.get("result");
            JSONArray jsonArray = (JSONArray) resultCommitAPI.get("data");

            for (Object item : jsonArray) {

                JSONObject jsonObject = (JSONObject) item;
                String commitPHID = str(jsonObject, "phid");

                ResponseEntity<String> commitDetailAPI = commitDetailRestCall(commitDetails, apiToken, commitPHID);
                JSONObject commitValues = paresAsObject(commitDetailAPI);
                JSONObject resultCommitDetail = (JSONObject) commitValues.get("result");
                JSONObject dataCommitDetail = (JSONObject) resultCommitDetail.get("data");
                JSONObject commitDetail = (JSONObject) dataCommitDetail.get("phid");

                String sha = str(commitDetail, "id");
                String author = str(commitDetail, "author");
                String message = str(commitDetail, "summary");
                long timestamp = Long.valueOf(str(commitDetail, "epoch"));
                JSONArray parents = (JSONArray) jsonObject.get("hashes");
                List<String> parentShas = new ArrayList<>();
                if (parents != null) {
                    for (Object parentObj : parents) {
                        parentShas.add(str((JSONObject) parentObj, "id"));
                    }
                }

                Commit commit = new Commit();
                commit.setTimestamp(System.currentTimeMillis());
                commit.setScmUrl(repo.getRepoUrl());
                commit.setScmBranch(repo.getBranch());
                commit.setScmRevisionNumber(sha);
                commit.setScmParentRevisionNumbers(parentShas);
                commit.setScmAuthor(author);
                commit.setScmCommitLog(message);
                commit.setScmCommitTimestamp(timestamp);
                commit.setType(parentShas.size() > 1 ? CommitType.Merge : CommitType.New);
                commit.setNumberOfChanges(1);
                commits.add(commit);
            }

            repo.setLastUpdated(System.currentTimeMillis());

        } catch (RestClientException re) {
            LOG.error("Failed to obtain information from API", re);
        } catch (URISyntaxException e) {
            LOG.error("Invalid uri: " + e.getMessage());
        }

        return commits;
    }

    private ResponseEntity<String> repoRestCall(URI uri, String phabricatorToken, String repoURL) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"},"
                + "\"constraints\": {\"uris\": [\"" + repoURL + "\"]},"
                + "\"queryKey\": \"active\"}";

        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, headers());
        return restOperations.exchange(uri, HttpMethod.POST, httpEntity, String.class);

    }

    private ResponseEntity<String> commitRestCall(URI uri, String phabricatorToken, String repositoryPHID) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"},"
                + " \"constraints\":{ \"repositories\": [\"" + repositoryPHID + "\"]}}";

        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, headers());
        return restOperations.exchange(uri, HttpMethod.POST, httpEntity, String.class);

    }

    private ResponseEntity<String> commitDetailRestCall(URI uri, String phabricatorToken, String commitPHID) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"}, "
                + "\"phids\":[\"" + commitPHID + "\"]}";

        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, headers());
        return restOperations.exchange(uri, HttpMethod.POST, httpEntity, String.class);

    }

    private HttpHeaders headers() {
        //Phabricator API Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        return headers;
    }

    private JSONObject paresAsObject(ResponseEntity<String> response) {
        try {
            return (JSONObject) new JSONParser().parse(response.getBody());
        } catch (ParseException pe) {
            LOG.error(pe.getMessage());
        }
        return new JSONObject();
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

}