package com.noptech.stira.domain.enumeration;

/**
 * The TicketSource enumeration.
 */
public enum TicketSource {
    STORM("STORM"), JIRA("JIRA");

    private final String name;

    private TicketSource(String name) {
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
