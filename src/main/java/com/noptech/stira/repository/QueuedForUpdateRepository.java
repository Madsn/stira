package com.noptech.stira.repository;

import com.noptech.stira.domain.QueuedForUpdate;

import com.noptech.stira.domain.enumeration.TicketSource;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.List;


/**
 * Spring Data JPA repository for the QueuedForUpdate entity.
 */
public interface QueuedForUpdateRepository extends JpaRepository<QueuedForUpdate,Long> {

    @Query("SELECT q FROM QueuedForUpdate q WHERE q.ticketKey = :ticketKey")
    QueuedForUpdate findByTicketKey(@Param("ticketKey") String ticketKey);


    @Query("SELECT q FROM QueuedForUpdate q WHERE q.ticketSource = :ticketSource ORDER BY q.addedToQueue DESC")
    List<QueuedForUpdate> getOldest(@Param("ticketSource") TicketSource ticketSource);
}
