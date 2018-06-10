package com.capitalone.dashboard.collector;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Bean to hold settings specific to the git collector.
 */
@Component
@ConfigurationProperties(prefix = "git")
public class GitSettings {

    private String cron;
    private String host;
    private String protocol;
    private String apiToken;
    private int firstRunHistoryDays;
    private int pageSize;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public int getFirstRunHistoryDays() {
        return firstRunHistoryDays;
    }

    public void setFirstRunHistoryDays(int firstRunHistoryDays) {
        this.firstRunHistoryDays = firstRunHistoryDays;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
