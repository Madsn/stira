package com.noptech.stira.domain.enumeration;

public enum TicketStatus {

    WAITING_SSE("Systematic"),
    WAITING_SKAT("SKAT"),
    WAITING_TDCH("TDCH"),
    UNKNOWN("Unknown"),
    CLOSED("Closed");

    private final String status;

    private TicketStatus(String status) {
        this.status = status;
    }

    public boolean equalsName(String otherStatus) {
        return (otherStatus == null) ? false : status.equals(otherStatus);
    }

    public String toString() {
        return this.status;
    }

    public static TicketStatus parseFromString(String text) {
        switch (text.trim()) {
            // Storm
            case "Pending customer: [SPOC] - SKAT Dias/Systematic":
                return TicketStatus.WAITING_SKAT;
            case "Pending external consultant":
                return TicketStatus.WAITING_SSE;
            case "Pending Service Delivery Manager":
                return TicketStatus.WAITING_TDCH;
            // Jira
            case "Close Requested":
                return TicketStatus.WAITING_SKAT;
            case "New":
                return TicketStatus.WAITING_SSE;
            case "In Progress":
                return TicketStatus.WAITING_SSE;
            case "Waiting for Customer":
                return TicketStatus.WAITING_SKAT;
            case "Waiting for Customer Test":
                return TicketStatus.WAITING_SKAT;
            case "Waiting for Subcontractor":
                return TicketStatus.WAITING_TDCH;
            case "Resolved":
                return TicketStatus.CLOSED;
            // Both
            case "Closed":
                return TicketStatus.CLOSED;
            /*
            case "TODO":
                return TicketStatus.WAITING_TDCH;
            */
            default:
                return TicketStatus.UNKNOWN;
        }
    }
}
