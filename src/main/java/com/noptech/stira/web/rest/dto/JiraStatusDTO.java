package com.noptech.stira.web.rest.dto;

import net.rcarz.jiraclient.Issue;

public class JiraStatusDTO {
    private String key;
    private String priority;
    private String status;

    public JiraStatusDTO() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void extractInfoFromIssue(Issue issue) {
        this.setKey(issue.getKey());
        this.setPriority(issue.getPriority().getName());
        this.setStatus(issue.getStatus().toString());
    }
}
