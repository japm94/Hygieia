package com.capitalone.dashboard.phabricator;

import com.capitalone.dashboard.collector.GitSettings;
import com.capitalone.dashboard.util.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.Arrays;


public class PhabricatorRestCall {
    private static final Log LOG = LogFactory.getLog(PhabricatorRestCall.class);

    public final GitSettings settings;

    public final RestOperations restOperations;

    @Autowired
    public PhabricatorRestCall(GitSettings settings,
                               Supplier<RestOperations> restOperationsSupplier) {
        this.settings = settings;
        this.restOperations = restOperationsSupplier.get();
    }

    public ResponseEntity<String> repoRestCall(URI uri, String phabricatorToken, String repoURL) {
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

    public ResponseEntity<String> commitRestCall(URI uri, String phabricatorToken, String repositoryPHID) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"},"
                + " \"constraints\":{ \"repositories\": [\"" + repositoryPHID + "\"]}}";

        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, headers());
        return restOperations.exchange(uri, HttpMethod.POST, httpEntity, String.class);

    }

    public ResponseEntity<String> commitDetailRestCall(URI uri, String phabricatorToken, String commitPHID) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"}, "
                + "\"phids\":[\"" + commitPHID + "\"]}";

        HttpEntity<?> httpEntity = new HttpEntity<Object>(body, headers());
        return restOperations.exchange(uri, HttpMethod.POST, httpEntity, String.class);

    }

    public ResponseEntity<String> commitParentsRestCall(URI uri, String phabricatorToken, String
            commitIdentif, String repoCallsign) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("POST " + uri);
        }

        //Phabricator API Boddy
        String body = "params={\"__conduit__\":{\"token\":\"" + phabricatorToken + "\"},"
                + "\"commit\": \"" + commitIdentif + "\","
                + "\"callsign\": \"" + repoCallsign + "\"}";

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

}