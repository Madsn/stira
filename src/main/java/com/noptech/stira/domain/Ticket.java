package com.noptech.stira.domain;

import com.noptech.stira.domain.enumeration.TicketStatus;
import net.rcarz.jiraclient.Issue;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Ticket.
 */
@Entity
@Table(name = "ticket")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "storm_key")
    private Long stormKey;

    @Column(name = "jira_key")
    private String jiraKey;

    @Column(name = "jira_title")
    private String jiraTitle;

    @Column(name = "storm_title")
    private String stormTitle;

    @Column(name = "jira_last_updated")
    private LocalDateTime jiraLastUpdated;

    @Column(name = "storm_last_updated")
    private LocalDateTime stormLastUpdated;

    @Column(name = "muted_until")
    private LocalDate mutedUntil;

    @Column(name = "jira_status")
    @Enumerated(EnumType.STRING)
    private TicketStatus jiraStatus;

    @Column(name = "storm_status")
    @Enumerated(EnumType.STRING)
    private TicketStatus stormStatus;

    public Ticket(Issue issue) {
        this.jiraKey = issue.getKey();
        LocalDateTime updated = LocalDateTime.parse(issue.getField("updated").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx"));
        this.jiraLastUpdated = updated;
        this.jiraTitle = issue.getSummary();
        this.jiraStatus = TicketStatus.parseFromString(issue.getStatus().getName());
        String customerRef = issue.getField("customer issue reference").toString();
        Pattern pattern = Pattern.compile("#\\d+");
        Matcher matcher = pattern.matcher(customerRef);
        String stormKey = matcher.group(0).replace("#", "");
        if (stormKey != null) {
            this.stormKey = Long.parseLong(stormKey);
        }
    }

    public Ticket() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStormKey() {
        return stormKey;
    }

    public void setStormKey(Long stormKey) {
        this.stormKey = stormKey;
    }

    public String getJiraKey() {
        return jiraKey;
    }

    public void setJiraKey(String jiraKey) {
        this.jiraKey = jiraKey;
    }

    public String getJiraTitle() {
        return jiraTitle;
    }

    public void setJiraTitle(String jiraTitle) {
        this.jiraTitle = jiraTitle;
    }

    public String getStormTitle() {
        return stormTitle;
    }

    public void setStormTitle(String stormTitle) {
        this.stormTitle = stormTitle;
    }

    public LocalDateTime getJiraLastUpdated() {
        return jiraLastUpdated;
    }

    public void setJiraLastUpdated(LocalDateTime jiraLastUpdated) {
        this.jiraLastUpdated = jiraLastUpdated;
    }

    public LocalDateTime getStormLastUpdated() {
        return stormLastUpdated;
    }

    public void setStormLastUpdated(LocalDateTime stormLastUpdated) {
        this.stormLastUpdated = stormLastUpdated;
    }

    public LocalDate getMutedUntil() {
        return mutedUntil;
    }

    public void setMutedUntil(LocalDate mutedUntil) {
        this.mutedUntil = mutedUntil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ticket ticket = (Ticket) o;
        if(ticket.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ticket{" +
            "id=" + id +
            ", stormKey='" + stormKey + "'" +
            ", jiraKey='" + jiraKey + "'" +
            ", jiraTitle='" + jiraTitle + "'" +
            ", stormTitle='" + stormTitle + "'" +
            ", jiraLastUpdated='" + jiraLastUpdated + "'" +
            ", stormLastUpdated='" + stormLastUpdated + "'" +
            ", mutedUntil ='" + mutedUntil + "'" +
            ", jiraStatus ='" + jiraStatus + "'" +
            ", stormStatus ='" + stormStatus + "'" +
            '}';
    }

    public TicketStatus getStormStatus() {
        return stormStatus;
    }

    public void setStormStatus(TicketStatus stormStatus) {
        this.stormStatus = stormStatus;
    }

    public TicketStatus getJiraStatus() {
        return jiraStatus;
    }

    public void setJiraStatus(TicketStatus jiraStatus) {
        this.jiraStatus = jiraStatus;
    }
}
