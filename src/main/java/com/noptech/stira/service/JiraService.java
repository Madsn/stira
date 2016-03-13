package com.noptech.stira.service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.noptech.stira.web.rest.dto.JiraStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@Service
@Transactional
@PropertySource("classpath:credentials.properties")
public class JiraService {

    private final Logger log = LoggerFactory.getLogger(JiraService.class);

    @Value("${jira.user}")
    private String jiraUser;
    @Value("${jira.password}")
    private String jiraPass;
    @Value("${jira.url}")
    private String jiraUrl;

    private JiraRestClient jiraClient;


    public JiraStatusDTO getStatus(String issueKeyParam) throws ExecutionException, InterruptedException {
        JiraStatusDTO jiraStatus = new JiraStatusDTO();

        if (jiraClient == null) {
            createJiraClient();
        }
        IssueRestClient ic = jiraClient.getIssueClient();

        Promise<Issue> issuePromise = ic.getIssue(issueKeyParam);
        Issue issue = issuePromise.claim();
        jiraStatus.extractInfoFromIssue(issue);
        return jiraStatus;
    }

    private void createJiraClient() {
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        try {
            URI uri = new URI(jiraUrl);
            log.debug("Creating restclient with uri: " + jiraUrl);
            BasicHttpAuthenticationHandler auth = new BasicHttpAuthenticationHandler(jiraUser, jiraPass);
            jiraClient = factory.create(uri, auth);
        } catch (URISyntaxException e) {
            log.error("Could not parse Jira URI from properties file. Value: " + jiraUrl);
            e.printStackTrace();
        }
    }
}
