package com.noptech.stira.service;

import com.noptech.stira.domain.Ticket;
import com.noptech.stira.repository.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class TicketService {

    private final Logger log = LoggerFactory.getLogger(TicketService.class);

    @Inject
    private TicketRepository ticketRepository;

    public void mergeFromJira(List<Ticket> ticketList) {
        for (Ticket ticket : ticketList) {
            mergeFromJira(ticket);
        }
    }

    public void mergeFromJira(Ticket ticket) {
        Ticket existingTicket = ticketRepository.findOneByJiraKey(ticket.getJiraKey());
        if (existingTicket == null) {
            if (ticket.getStormKey() != null) {
                existingTicket = ticketRepository.findOneByStormKey(ticket.getStormKey());
            }
        }
        if (existingTicket == null) {
            existingTicket = ticket;
        } else {
            existingTicket.mergeValuesFrom(ticket);
        }
        ticketRepository.save(existingTicket);
    }

    public void mergeFromStorm(List<Ticket> ticketList) {
        for (Ticket ticket : ticketList) {
            mergeFromStorm(ticket);
        }
    }

    public void mergeFromStorm(Ticket ticket) {
        Ticket existingTicket = ticketRepository.findOneByStormKey(ticket.getStormKey());
        if (existingTicket == null) {
            if (ticket.getJiraKey() != null) {
                existingTicket = ticketRepository.findOneByJiraKey(ticket.getStormKey());
            }
        }
        if (existingTicket == null) {
            existingTicket = ticket;
        } else {
            existingTicket.mergeValuesFrom(ticket);
        }
        ticketRepository.save(existingTicket);
    }

    public List<Ticket> findWithMissingFields() {
        return ticketRepository.findWithMissingFields();
    }

    public void flagTicketByStormKey(String ticketKey) {
        Ticket t = ticketRepository.findOneByStormKey(ticketKey);
        if (t != null) {
            t.setFlagged(true);
            ticketRepository.save(t);
        }
    }
}
