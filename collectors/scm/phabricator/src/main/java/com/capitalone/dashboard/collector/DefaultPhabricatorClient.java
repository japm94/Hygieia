package com.capitalone.dashboard.collector;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.CommitType;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.phabricator.PhabricatorBuildURI;
import com.capitalone.dashboard.phabricator.PhabricatorRestCall;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a git client to connect to an Phabricator <i>Server</i>.
 */

@Component
public class DefaultPhabricatorClient implements GitClient {
    private static final Log LOG = LogFactory.getLog(DefaultPhabricatorClient.class);

    private final PhabricatorBuildURI endpoint;

    private final GitSettings settings;


    private final PhabricatorRestCall rest;

    @Autowired
    public DefaultPhabricatorClient(PhabricatorBuildURI endpoint, GitSettings settings, PhabricatorRestCall rest) {
        this.endpoint = endpoint;
        this.settings = settings;
        this.rest = rest;
    }


    @SuppressWarnings("PMD.NPathComplexity")
    @Override
    public List<Commit> getCommits(GitRepo repo, boolean firstRun) {
        List<Commit> commits = new ArrayList<>();

        try {

            // Phabricator Token
            String apiToken = settings.getApiToken();

            //Phabricator endpoints
            URI repoURI = endpoint.buildRepoUrl();
            URI commitURI = endpoint.buildCommitUrl();
            URI commitDetailsURI = endpoint.buildCommitDetailUrl();
            URI commitParentsURI = endpoint.buildParentURL();

            // Get Repo PHID and CALLSIGN
            JSONArray repoValues = rest.repoRestCall(repoURI, apiToken, repo.getRepoUrl());
            String repositoryPHID = null;
            String callsignRepo = null;
            for (Object item : repoValues) {
                JSONObject jsonObject = (JSONObject) item;
                repositoryPHID = str(jsonObject, "phid");
                JSONObject fields = (JSONObject) jsonObject.get("fields");
                callsignRepo = str(fields, "callsign");
            }
            // Get Commit Values
            JSONArray commitValues = rest.commitRestCall(commitURI, apiToken, repositoryPHID);


            for (Object item : commitValues) {

                JSONObject obj = (JSONObject) item;
                String commitPHID = str(obj, "phid");

                JSONObject commitDetail = rest.commitDetailRestCall(commitDetailsURI, apiToken, commitPHID);
                String sha = str(commitDetail, "id");
                String author = str(commitDetail, "author");
                String message = str(commitDetail, "summary");
                long timestamp = Long.valueOf(str(commitDetail, "epoch"));
                String commitIdentf = str(commitDetail, "identifier");

                // Get Parents

                List<String> commitParents = rest.commitParentsRestCall(commitParentsURI, apiToken, commitIdentf, callsignRepo);

                Commit commit = new Commit();
                commit.setTimestamp(System.currentTimeMillis());
                commit.setScmUrl(repo.getRepoUrl());
                commit.setScmBranch(repo.getBranch());
                commit.setScmRevisionNumber(sha);
                commit.setScmParentRevisionNumbers(commitParents);
                commit.setScmAuthor(author);
                commit.setScmCommitLog(message);
                commit.setScmCommitTimestamp(timestamp);
                commit.setType(commitParents.size() > 1 ? CommitType.Merge : CommitType.New);
                commit.setNumberOfChanges(1);
                commits.add(commit);
            }

            repo.setLastUpdated(System.currentTimeMillis());

        } catch (RestClientException re) {
            LOG.error("Failed to obtain information from API", re);
        } catch (URISyntaxException e) {
            LOG.error("Invalid uri: " + e.getMessage());
        } catch (ParseException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        return commits;
    }

    private String str(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? null : value.toString();
    }
}