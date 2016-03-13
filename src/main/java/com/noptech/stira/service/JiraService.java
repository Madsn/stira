package com.noptech.stira.service;

import com.noptech.stira.web.rest.dto.JiraStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JiraService {

    private final Logger log = LoggerFactory.getLogger(JiraService.class);

    public JiraStatusDTO getStatus(Long ticketIdParam) {
        JiraStatusDTO jiraStatus = new JiraStatusDTO();
        JiraRestClient jrc = new JiraRestClient();
        return null;
    }
}
