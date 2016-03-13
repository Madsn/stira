package com.noptech.stira.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.noptech.stira.service.JiraService;
import com.noptech.stira.web.rest.dto.JiraStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class JiraResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    private JiraService jiraService;

    @RequestMapping(value = "/jira/{issueKeyParam:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public JiraStatusDTO getStatus(@PathVariable String issueKeyParam) throws ExecutionException, InterruptedException {
        JiraStatusDTO jiraStatus = jiraService.getStatus(issueKeyParam);
        return jiraStatus;
    }
}
