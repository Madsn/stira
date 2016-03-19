package com.noptech.stira.repository;

import com.noptech.stira.domain.QueueSource;
import com.noptech.stira.domain.enumeration.TicketSource;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the QueueSource entity.
 */
public interface QueueSourceRepository extends JpaRepository<QueueSource, Long> {

    QueueSource findOneByTicketSource(TicketSource source);

}
