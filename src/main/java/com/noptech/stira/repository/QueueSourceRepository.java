package com.noptech.stira.repository;

import com.noptech.stira.domain.QueueSource;

import com.noptech.stira.domain.enumeration.TicketSource;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the QueueSource entity.
 */
public interface QueueSourceRepository extends JpaRepository<QueueSource,Long> {

    @Query("SELECT qs FROM QueueSource qs WHERE qs.ticketSource = :sourceName")
    QueueSource findByTicketSource(@Param("sourceName") TicketSource storm);
}
