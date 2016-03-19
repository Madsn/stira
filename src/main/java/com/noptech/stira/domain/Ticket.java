package com.noptech.stira.domain;

import com.noptech.stira.domain.enumeration.TicketStatus;
import net.rcarz.jiraclient.Field;
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
    private String stormKey;

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

    @Column(name = "flagged")
    private boolean flagged;

    public Ticket(Issue issue) {
        this.jiraKey = issue.getKey();
        LocalDateTime updated = LocalDateTime.parse(issue.getField("updated").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx"));
        this.jiraLastUpdated = updated;
        this.jiraTitle = issue.getSummary();
        this.jiraStatus = TicketStatus.parseFromString(issue.getStatus().getName());
        Object issueRef = issue.getField("customfield_11502");
        if (issueRef != null) {
            String issueRefString = Field.getString(issueRef);
            Pattern pattern = Pattern.compile("#(\\d+)");
            Matcher matcher = pattern.matcher(issueRefString);
            if (matcher.find()) {
                this.stormKey = matcher.group(0).replace("#", "");
            }
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

    public String getStormKey() {
        return stormKey;
    }

    public void setStormKey(String stormKey) {
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
            ", flagged ='" + flagged + "'" +
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

    public void mergeValuesFrom(Ticket newValues) {
        if (newValues.getJiraKey() != null)
            this.setJiraKey(newValues.getJiraKey());
        if (newValues.getJiraLastUpdated() != null)
            this.setJiraLastUpdated(newValues.getJiraLastUpdated());
        if (newValues.getJiraStatus() != null)
            this.setJiraStatus(newValues.getJiraStatus());
        if (newValues.getJiraTitle() != null)
            this.setJiraTitle(newValues.getJiraTitle());
        if (newValues.getMutedUntil() != null)
            this.setMutedUntil(newValues.getMutedUntil());
        if (newValues.getStormKey() != null)
            this.setStormKey(newValues.getStormKey());
        if (newValues.getStormLastUpdated() != null)
            this.setStormLastUpdated(newValues.getStormLastUpdated());
        if (newValues.getStormLastUpdated() != null)
            this.setStormLastUpdated(newValues.getStormLastUpdated());
        if (newValues.getStormStatus() != null)
            this.setStormStatus(newValues.getStormStatus());
        if (newValues.getStormTitle() != null)
            this.setStormTitle(newValues.getStormTitle());
        if (newValues.isFlagged())
            this.flagged = newValues.isFlagged();
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isFlagged() {
        return flagged;
    }
}
