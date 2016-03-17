package com.noptech.stira.repository;

import com.noptech.stira.domain.QueuedForUpdate;
import com.noptech.stira.domain.enumeration.TicketSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * Spring Data JPA repository for the QueuedForUpdate entity.
 */
public interface QueuedForUpdateRepository extends JpaRepository<QueuedForUpdate,Long> {

    @Query("SELECT q FROM QueuedForUpdate q WHERE q.ticketKey = :ticketKey")
    QueuedForUpdate findByTicketKey(@Param("ticketKey") String ticketKey);


    @Query("SELECT q FROM QueuedForUpdate q WHERE q.ticketSource = :ticketSource ORDER BY q.addedToQueue ASC")
    List<QueuedForUpdate> getOldest(@Param("ticketSource") TicketSource ticketSource);
}
