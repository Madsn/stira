package com.noptech.stira.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import com.noptech.stira.domain.enumeration.TicketSource;

/**
 * A QueuedForUpdate.
 */
@Entity
@Table(name = "queued_for_update")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class QueuedForUpdate implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_source")
    private TicketSource ticketSource;

    @Column(name = "added_to_queue")
    private LocalDateTime addedToQueue;

    @Column(name = "ticket_key")
    private String ticketKey;

    public QueuedForUpdate(){}

    public QueuedForUpdate(Ticket t, TicketSource source) throws Exception {
        if (source.equals(TicketSource.JIRA)) {
            this.ticketKey = t.getJiraKey();
            this.addedToQueue = t.getJiraLastUpdated();
        } else if (source.equals(TicketSource.STORM)) {
            this.ticketKey = t.getStormKey();
            this.addedToQueue = t.getStormLastUpdated();
        } else {
            throw new Exception("Unknown ticket source: " + source.toString());
        }
        this.ticketSource = source;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TicketSource getTicketSource() {
        return ticketSource;
    }

    public void setTicketSource(TicketSource ticketSource) {
        this.ticketSource = ticketSource;
    }

    public LocalDateTime getAddedToQueue() {
        return addedToQueue;
    }

    public void setAddedToQueue(LocalDateTime addedToQueue) {
        this.addedToQueue = addedToQueue;
    }

    public String getTicketKey() {
        return ticketKey;
    }

    public void setTicketKey(String ticketKey) {
        this.ticketKey = ticketKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueuedForUpdate queuedForUpdate = (QueuedForUpdate) o;
        if(queuedForUpdate.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, queuedForUpdate.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QueuedForUpdate{" +
            "id=" + id +
            ", ticketSource='" + ticketSource + "'" +
            ", addedToQueue='" + addedToQueue + "'" +
            ", ticketKey='" + ticketKey + "'" +
            '}';
    }
}
