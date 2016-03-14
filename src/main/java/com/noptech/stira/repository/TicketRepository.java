package com.noptech.stira.repository;

import com.noptech.stira.domain.Ticket;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ticket entity.
 */
public interface TicketRepository extends JpaRepository<Ticket,Long> {

}
