package com.noptech.stira.web.rest.dto;

public class JiraStatusDTO {
    private long id;
    private short priority;
    private String status;

    public JiraStatusDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public short getPriority() {
        return this.priority;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
