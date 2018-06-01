package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.phabricator.PhabricatorAPIEndpoint;
import com.capitalone.dashboard.phabricator.PhabricatorRestCall;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a git client to connect to an Phabricator <i>Server</i>.
 */

@Component("phabricator")
public class DefaultPhabricatorClient implements GitClient {
    private static final Log LOG = LogFactory.getLog(DefaultPhabricatorClient.class);

    private GitSettings settings;

    private PhabricatorAPIEndpoint endpoint;

    private PhabricatorRestCall restCall;


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
            URI commitParents = endpoint.buildParentURL();

            // Get Repo PHID and CALLSIGN
            ResponseEntity<String> repoAPI = restCall.repoRestCall(repoEndpoint, apiToken, repo.getRepoUrl());
            JSONObject repoJSON = paresAsObject(repoAPI);
            JSONObject repoResult = (JSONObject) repoJSON.get("result");
            JSONArray repoData = (JSONArray) repoResult.get("data");
            String repositoryPHID = null;
            String callsignRepo = null;
            if (repoData != null) {
                for (Object repoValues : repoData) {
                    JSONObject repoOject = (JSONObject) repoValues;
                    repositoryPHID = str((JSONObject) repoOject, "phid");
                    JSONObject fields = (JSONObject) repoOject.get("fields");
                    callsignRepo = str((JSONObject) fields, "callsign");

                }
            }

            // Get Commit Values
            ResponseEntity<String> commitAPI = restCall.commitRestCall(commitEndpoint, apiToken, repositoryPHID);
            JSONObject jsonParentObject = paresAsObject(commitAPI);
            JSONObject resultCommitAPI = (JSONObject) jsonParentObject.get("result");
            JSONArray jsonArray = (JSONArray) resultCommitAPI.get("data");

            for (Object item : jsonArray) {

                JSONObject jsonObject = (JSONObject) item;
                String commitPHID = str(jsonObject, "phid");

                ResponseEntity<String> commitDetailRest = restCall.commitDetailRestCall(commitDetails, apiToken, commitPHID);
                JSONObject commitValues = paresAsObject(commitDetailRest);
                JSONObject resultCommitDetail = (JSONObject) commitValues.get("result");
                JSONObject dataCommitDetail = (JSONObject) resultCommitDetail.get("data");
                JSONObject commitDetail = (JSONObject) dataCommitDetail.get(commitPHID);
                String sha = str(commitDetail, "id");
                String author = str(commitDetail, "author");
                String message = str(commitDetail, "summary");
                long timestamp = Long.valueOf(str(commitDetail, "epoch"));
                String commitIdentf = str(commitDetail, "identifier");

                // Get Parents
                ResponseEntity<String> commitParentsRest = restCall.commitParentsRestCall(commitParents, apiToken, commitIdentf, callsignRepo);
                JSONObject commitParent = paresAsObject(commitParentsRest);
                JSONArray parents = (JSONArray) commitParent.get("result");
                List<String> parentShas = new ArrayList<String>();
                if (parents != null) {
                    for (int i = 0; i < parents.size(); i++) {
                        parentShas.add(parents.toString());
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