package com.noptech.stira.domain;

import com.noptech.stira.web.rest.dto.StormStatusDTO;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

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
            '}';
    }
}
