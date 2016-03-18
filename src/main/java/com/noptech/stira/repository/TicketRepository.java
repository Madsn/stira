package com.noptech.stira.repository;

import com.noptech.stira.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Ticket entity.
 */
public interface TicketRepository extends JpaRepository<Ticket,Long> {

    Ticket findOneByStormKey(Long stormKey);

    Ticket findOneByJiraKey(String jiraKey);
}
