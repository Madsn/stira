package com.noptech.stira.service;

import com.noptech.stira.security.AuthoritiesConstants;
import com.noptech.stira.web.rest.dto.JiraStatusDTO;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private JiraClient jiraClient;


    public JiraStatusDTO getStatus(String issueKeyParam) throws ExecutionException, InterruptedException {
        JiraStatusDTO jiraStatus = new JiraStatusDTO();

        if (jiraClient == null) {
            createJiraClient();
        }
        Issue issue = null;
        try {
            issue = jiraClient.getIssue(issueKeyParam);
        } catch (JiraException e) {
            log.error("Exception finding jira issue with key: " + issueKeyParam);
            e.printStackTrace();
        }
        jiraStatus.extractInfoFromIssue(issue);

        return jiraStatus;
    }

    private void createJiraClient() {
        BasicCredentials creds = new BasicCredentials(jiraUser, jiraPass);
        jiraClient = new JiraClient(jiraUrl, creds);
    }

    @Scheduled(fixedDelay = 500000)
    public void runJiraJob() {
        // First check jira for updated issues, add to queue

        // Update Queue count

        // Process oldest updated issue from queue
        log.debug("Running jira job");
    }
}
