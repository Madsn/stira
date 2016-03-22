package com.noptech.stira.repository;

import com.noptech.stira.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Ticket entity.
 */
public interface TicketRepository extends JpaRepository<Ticket,Long> {

    Ticket findOneByStormKey(String stormKey);

    Ticket findOneByJiraKey(String jiraKey);

    @Query("SELECT t from Ticket t where t.jiraKey is not null and t.stormLastUpdated is null and t.stormKey is not null and t.flagged = false")
    List<Ticket> findWithMissingStormFields();

    @Query("SELECT t from Ticket t where (t.jiraKey is not null and t.stormLastUpdated is null)" +
        " or (t.stormKey is not null and t.jiraLastUpdated is null) and t.flagged = false")
    List<Ticket> findWithMissingFields();

    @Query("SELECT t from Ticket t where t.stormKey is not null and t.stormStatus <> t.jiraStatus")
    List<Ticket> findWithDifferentStatus();

    @Query("SELECT t from Ticket t where t.stormKey is not null and t.stormStatus = 'WAITING_SSE'" +
        " and (t.jiraStatus <> 'WAITING_SSE' or t.jiraStatus is null)")
    List<Ticket> findPendingSSEWithDifferentStatus();
}
