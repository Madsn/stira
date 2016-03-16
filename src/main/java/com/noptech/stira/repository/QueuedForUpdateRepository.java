package com.noptech.stira.repository;

import com.noptech.stira.domain.QueuedForUpdate;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the QueuedForUpdate entity.
 */
public interface QueuedForUpdateRepository extends JpaRepository<QueuedForUpdate,Long> {

    @Query("SELECT q FROM QueuedForUpdate q WHERE q.ticketKey = :ticketKey")
    QueuedForUpdate findByTicketKey(@Param("ticketKey") String ticketKey);
}
