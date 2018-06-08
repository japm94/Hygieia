package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.phabricator.PhabricatorBuildURI;
import com.capitalone.dashboard.phabricator.PhabricatorRestCall;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a git client to connect to an Phabricator <i>Server</i>.
 */

@Component
public class DefaultPhabricatorClient implements GitClient {
    private static final Log LOG = LogFactory.getLog(DefaultPhabricatorClient.class);

    private GitSettings settings;

    private PhabricatorBuildURI endpoint;

    private PhabricatorRestCall restCall;


    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public List<Commit> getCommits(GitRepo repo, boolean firstRun) {
        List<Commit> commits = new ArrayList<>();

        try {

            // Phabricator Token
            String apiToken = settings.getToken();

            //Phabricator endpoints
            String repoURL = endpoint.buildRepoUrl();
            String commitURL = endpoint.buildCommitUrl();
            String commitDetailsURL = endpoint.buildCommitDetailUrl();
            String commitParentsURL = endpoint.buildParentURL();

            // Get Repo PHID and CALLSIGN
            JSONObject repoValues = restCall.repoRestCall(repoURL, apiToken, repo.getRepoUrl());
            String repositoryPHID = repoValues.getString("repoPHID");
            String callsignRepo = repoValues.getString("callsign");

            // Get Commit Values
            JSONArray commitValues = restCall.commitRestCall(commitURL, apiToken, repositoryPHID);


            for (int i = 0; i < commitValues.length(); i++) {

                JSONObject obj = commitValues.getJSONObject(i);
                String commitPHID = obj.getString("phid");

                JSONObject commitDetail = restCall.commitDetailRestCall(commitDetailsURL, apiToken, commitPHID);
                String sha = commitDetail.getString("id");
                String author = commitDetail.getString("author");
                String message = commitDetail.getString("summary");
                long timestamp = Long.valueOf(commitDetail.getString("epoch"));
                String commitIdentf = commitDetail.getString("identifier");

                // Get Parents
                JSONArray commitParents = restCall.commitParentsRestCall(commitParentsURL, apiToken, commitIdentf, callsignRepo);
                List<String> parentShas = new ArrayList<String>();
                if (commitParents != null) {
                    for (int j = 0; j < commitParents.length(); j++) {
                        parentShas.add(commitParents.getString(j));
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
        } catch (UnirestException e) {
            LOG.error("Error", e);
        }

        return commits;
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }

}