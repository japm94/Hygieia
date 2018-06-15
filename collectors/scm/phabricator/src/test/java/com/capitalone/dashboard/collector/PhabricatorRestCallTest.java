//package com.capitalone.dashboard.collector;
//
//import com.capitalone.dashboard.phabricator.PhabricatorBuildURI;
//import com.capitalone.dashboard.phabricator.PhabricatorRestCall;
//import com.capitalone.dashboard.util.Supplier;
//import org.apache.commons.io.IOUtils;
//import org.apache.http.ParseException;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.Matchers;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestOperations;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//
//import static org.mockito.Matchers.eq;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class PhabricatorRestCallTest {
//    @Mock private Supplier<RestOperations> restOperationsSupplier;
//    @Mock private RestOperations rest;
//    @Mock private GitSettings settings;
//    @Mock private PhabricatorBuildURI uri;
//
//    @Captor private ArgumentCaptor<HttpEntity<?>> httpEntityCaptor;
//    private PhabricatorRestCall client;
//    @Before
//    public void init() {
//        when(restOperationsSupplier.get()).thenReturn(rest);
//        settings = new GitSettings( );
//        settings.setPageSize(25);
//
//        client = new DefaultPhabricatorClient(restOperationsSupplier);
//    }
//
//    private final String repoUrl = "https://pb-dc.alm-latam.accenture.com/source/ijp.git";
//    private final URI repoURI = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.repository.search");
//    private final URI commitURI = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.commit.search");
//    private final URI commitdetailURI = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.querycommits");
//    private final URI commitParentsURI = URI.create("https://pb-dc.alm-latam.accenture.com/api/diffusion.commitparentsquery");
//    private final String repoPHID = "PHID-REPO-hkhdlgvdfofe5u3rfdqm";
//    private final String commitPHID = "PHID-CMIT-tcj6u3qoi3dqklnj3q4s";
//    private final String apiToken = "api-bmoeekxkmv6cfo6ry6jwjr2bbolw";
//    private final String commitIdentif = "3f83c3a0f1acdc3f1aefbd8748920f3c7c5bee3a";
//    private final String repoCallsign = "PBREPOTEST";
//
//    @Test
//    public void repoRestCallTest() throws IOException, ParseException {
//        String repoJson = getJson("/phabricator/repo.json");
//        when(rest.exchange(eq(repoURI), eq(HttpMethod.POST), Matchers.any(HttpEntity.class), eq(String.class))).thenReturn(new ResponseEntity<>(repoJson, HttpStatus.OK));
//    }
//
//    @Test
//    public void commitRestCallTest() throws IOException {
//        String commitJson = getJson("/phabricator/commit.json");
//        when(rest.exchange(eq(commitURI), eq(HttpMethod.POST), Matchers.any(HttpEntity.class), eq(String.class)))
//                .thenReturn(new ResponseEntity<>(commitJson, HttpStatus.OK));
//    }
//
//    @Test
//    public void commitDetailRestCallTest() throws IOException {
//        String commitDetailJson = getJson("/phabricator/commitdetail.json");
//        when(rest.exchange(eq(commitdetailURI), eq(HttpMethod.POST), Matchers.any(HttpEntity.class), eq(String.class)))
//                .thenReturn(new ResponseEntity<>(commitDetailJson, HttpStatus.OK));
//
//    }
//
//    @Test
//    public void commitParentsRestCallTest() throws IOException {
//        String commitParentsJson = getJson("/phabricator/commitparents.json");
//        when(rest.exchange(eq(commitParentsURI), eq(HttpMethod.POST), Matchers.any(HttpEntity.class), eq(String.class)))
//                .thenReturn(new ResponseEntity<>(commitParentsJson, HttpStatus.OK));
//    }
//
//    private String getJson(String fileName) throws IOException {
//        InputStream inputStream = PhabricatorRestCallTest.class.getResourceAsStream(fileName);
//        return IOUtils.toString(inputStream);
//    }
//}