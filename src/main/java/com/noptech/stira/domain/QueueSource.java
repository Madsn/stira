package com.noptech.stira.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.time.LocalDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

import com.noptech.stira.domain.enumeration.TicketSource;

/**
 * A QueueSource.
 */
@Entity
@Table(name = "queue_source")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class QueueSource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_source")
    private TicketSource ticketSource;
    
    @Column(name = "last_added_ticket")
    private LocalDate lastAddedTicket;
    
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

    public LocalDate getLastAddedTicket() {
        return lastAddedTicket;
    }
    
    public void setLastAddedTicket(LocalDate lastAddedTicket) {
        this.lastAddedTicket = lastAddedTicket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueueSource queueSource = (QueueSource) o;
        if(queueSource.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, queueSource.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "QueueSource{" +
            "id=" + id +
            ", ticketSource='" + ticketSource + "'" +
            ", lastAddedTicket='" + lastAddedTicket + "'" +
            '}';
    }
}
