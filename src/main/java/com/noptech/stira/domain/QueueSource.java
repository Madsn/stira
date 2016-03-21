package com.noptech.stira.domain;

import com.noptech.stira.domain.enumeration.TicketSource;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A QueueSource.
 */
@Entity
@Table(name = "queue_source")
@NamedQueries({
    @NamedQuery(name=QueueSource.FIND_ALL, query = "SELECT q from QueueSource q"),
    @NamedQuery(name=QueueSource.FIND_BY_TICKET_SOURCE, query = "SELECT q from QueueSource q where q.ticketSource = :ticketSource")
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class QueueSource implements Serializable {

    public static final String FIND_BY_TICKET_SOURCE = "QueueSource.findByTicketSource";
    public static final String FIND_ALL = "QueueSource.findAll";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_source")
    private TicketSource ticketSource;

    @Column(name = "last_added_ticket")
    private ZonedDateTime lastAddedTicket;

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

    public ZonedDateTime getLastAddedTicket() {
        return lastAddedTicket;
    }

    public void setLastAddedTicket(ZonedDateTime lastAddedTicket) {
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
        return !(queueSource.id == null || id == null) && Objects.equals(id, queueSource.id);
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
