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
            if (existingTicket == null) {
                existingTicket = ticket;
            }
        }
        existingTicket.setJiraLastUpdated(ticket.getJiraLastUpdated());
        existingTicket.setJiraStatus(ticket.getJiraStatus());
        existingTicket.setJiraTitle(ticket.getJiraTitle());
        if (ticket.getStormKey() != null) {
            existingTicket.setStormKey(ticket.getStormKey());
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
            existingTicket = ticket;
        } else {
            existingTicket.setStormLastUpdated(ticket.getJiraLastUpdated());
            existingTicket.setJiraStatus(ticket.getJiraStatus());
            existingTicket.setJiraTitle(ticket.getJiraTitle());
        }
        ticketRepository.save(existingTicket);
    }

}
