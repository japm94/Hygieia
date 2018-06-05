package com.capitalone.dashboard.collector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.capitalone.dashboard.model.Commit;
import com.capitalone.dashboard.model.GitRepo;
import com.capitalone.dashboard.util.Supplier;
import com.capitalone.dashboard.phabricator.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPhabricatorClientTest {
	@Mock private Supplier<RestOperations> restOperationsSupplier;
	@Mock private RestOperations rest;
	@Mock private GitSettings settings;
	@Mock private PhabricatorRestCall restCall;
	@Mock private PhabricatorBuildURI endpoint;
	
	@Captor private ArgumentCaptor<HttpEntity<?>> httpEntityCaptor;
	
	private DefaultPhabricatorClient client;
	
    @Before
    public void init() {
        when(restOperationsSupplier.get()).thenReturn(rest);
        settings = new GitSettings();
    	settings.setPageSize(25);
    	
        client = new DefaultPhabricatorClient();
    }
	
    @Test
    public void testGetCommits() throws IOException {
    	// Note that there always is paging even if results only take 1 page
    	String jsonResponse1 = getJson("/phabricator-server/repo.json");
    	String jsonResponse2 = getJson("/phabricator-server/commit.json");
		String jsonResponse3 = getJson("/phabricator-server/commitdetail.json");
		String jsonResponse4 = getJson("/phabricator-server/commitparents.json");

    	GitRepo repo = new GitRepo();
    	String repoUrl = "https://pb-dc.alm-latam.accenture.com/source/pbrepotest.git";
    	repo.setRepoUrl(repoUrl);
    	repo.getOptions().put("url", repoUrl);
    	repo.setBranch("master");
    	
        List<Commit> commits = client.getCommits(repo, true);
        
        assertEquals(2, commits.size());
        
        assertTrue(0 != commits.get(0).getTimestamp());
        assertEquals(repoUrl, commits.get(0).getScmUrl());
        assertEquals("215e5a6cbbda3a0cf4271a7e7c799306d3adb9ad", commits.get(0).getScmRevisionNumber());
        assertEquals("billybob", commits.get(0).getScmAuthor());
        assertEquals("Message 1", commits.get(0).getScmCommitLog());
        assertEquals(2, commits.get(0).getScmParentRevisionNumbers().size());
        assertEquals("9097aee6916a1883945b9cf9b77d351dc6802307", commits.get(0).getScmParentRevisionNumbers().get(0));
        assertEquals("30a9559513e471fb8f1deff10bd8823ad74a2fab", commits.get(0).getScmParentRevisionNumbers().get(1));
        assertEquals(1463771960000L, commits.get(0).getScmCommitTimestamp());
        
        assertTrue(0 != commits.get(1).getTimestamp());
        assertEquals(repoUrl, commits.get(1).getScmUrl());
        assertEquals("30a9559513e471fb8f1deff10bd8823ad74a2fab", commits.get(1).getScmRevisionNumber());
        assertEquals("billybob", commits.get(1).getScmAuthor());
        assertEquals("Message 2", commits.get(1).getScmCommitLog());
        assertEquals(1, commits.get(1).getScmParentRevisionNumbers().size());
        assertEquals("9097aee6916a1883945b9cf9b77d351dc6802307", commits.get(1).getScmParentRevisionNumbers().get(0));
        assertEquals(1463771869000L, commits.get(1).getScmCommitTimestamp());
    }
    
    private String getJson(String fileName) throws IOException {
        InputStream inputStream = DefaultPhabricatorClientTest.class.getResourceAsStream(fileName);
        return IOUtils.toString(inputStream);
    }
}
