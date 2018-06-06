package com.capitalone.dashboard.collector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Assert;

/**
 * Bean to hold settings specific to the git collector.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = GitSettings.class)
@TestPropertySource("classpath:application.properties")
public class GitSettingsTest {

    @Autowired
    private GitSettings settings;

    @Test
    public void cronTest() {
        String cron = settings.getCron();
        System.out.println(cron);

        Assert.assertEquals("0 * * * * *", cron);
    }

}
