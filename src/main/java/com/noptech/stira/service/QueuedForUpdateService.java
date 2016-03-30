package com.noptech.stira.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noptech.stira.domain.QueuedForUpdate;
import com.noptech.stira.domain.Ticket;
import com.noptech.stira.domain.enumeration.TicketSource;
import com.noptech.stira.repository.QueuedForUpdateRepository;

/**
 * Service Implementation for managing QueuedForUpdate.
 */
@Service
@Transactional
public class QueuedForUpdateService {

    private final Logger log = LoggerFactory.getLogger(QueuedForUpdateService.class);

    @Inject
    private QueuedForUpdateRepository queuedForUpdateRepository;

    public void addToQueue(List<Ticket> tickets) throws Exception {
        for (Ticket t : tickets) {
            QueuedForUpdate q = new QueuedForUpdate();
            if (t.getStormKey() != null && t.getJiraKey() != null) {
                if (t.getStormLastUpdated() == null) {
                    q.setTicketSource(TicketSource.STORM);
                    q.setTicketKey(t.getStormKey());
                    q.setAddedToQueue(t.getJiraLastUpdated());
                } else if (t.getJiraLastUpdated() == null) {
                    q.setTicketSource(TicketSource.JIRA);
                    q.setTicketKey(t.getJiraKey());
                    q.setAddedToQueue(t.getStormLastUpdated());
                } else {
                    q.setTicketSource(TicketSource.STORM);
                    q.setTicketKey(t.getStormKey());
                    q.setAddedToQueue(t.getStormLastUpdated());
                }
            } else if (t.getStormKey() != null && t.getStormLastUpdated() == null) {
                q.setTicketSource(TicketSource.STORM);
                q.setAddedToQueue(t.getStormLastUpdated());
                q.setTicketKey(t.getStormKey());
            } else if (t.getStormKey() != null && t.getJiraLastUpdated() == null) {
                q.setTicketSource(TicketSource.STORM);
                q.setAddedToQueue(t.getStormLastUpdated());
                q.setTicketKey(t.getStormKey());
            } else if (t.getStormKey() != null) {
                q.setTicketSource(TicketSource.STORM);
                q.setTicketKey(t.getStormKey());
                q.setAddedToQueue(t.getStormLastUpdated());
            } else if (t.getJiraKey() != null) {
                q.setTicketSource(TicketSource.JIRA);
                q.setTicketKey(t.getJiraKey());
                q.setAddedToQueue(t.getJiraLastUpdated());
            } else {
                throw new Exception("Did not expect ticket with status: " + t.toString());
            }
            log.debug("Saving queuedForUpdate entity: " + q.toString());
            QueuedForUpdate oldQ = queuedForUpdateRepository.findByTicketKeyAndSource(q.getTicketKey(), q.getTicketSource());
            if (oldQ == null) {
                queuedForUpdateRepository.save(q);
            } else {
                oldQ.setAddedToQueue(q.getAddedToQueue());
                queuedForUpdateRepository.save(oldQ);
            }
        }
    }

    /**
     * Save a queuedForUpdate.
     *
     * @return the persisted entity
     */
    public QueuedForUpdate save(QueuedForUpdate queuedForUpdate) {
        log.debug("Request to save QueuedForUpdate : {}", queuedForUpdate);
        QueuedForUpdate result = queuedForUpdateRepository.save(queuedForUpdate);
        return result;
    }

    /**
     * get all the queuedForUpdates.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<QueuedForUpdate> findAll() {
        log.debug("Request to get all QueuedForUpdates");
        List<QueuedForUpdate> result = queuedForUpdateRepository.findAll();
        return result;
    }

    /**
     * get one queuedForUpdate by id.
     *
     * @return the entity
     */
    @Transactional(readOnly = true)
    public QueuedForUpdate findOne(Long id) {
        log.debug("Request to get QueuedForUpdate : {}", id);
        QueuedForUpdate queuedForUpdate = queuedForUpdateRepository.findOne(id);
        return queuedForUpdate;
    }

    /**
     * delete the  queuedForUpdate by id.
     */
    public void delete(Long id) {
        log.debug("Request to delete QueuedForUpdate : {}", id);
        queuedForUpdateRepository.delete(id);
    }

    public QueuedForUpdate getNext(TicketSource ticketSource) throws Exception {
        List<QueuedForUpdate> updates = queuedForUpdateRepository.getOldest(ticketSource);
        if (updates != null && updates.size() > 0) {
            QueuedForUpdate q = updates.get(0);
            queuedForUpdateRepository.delete(updates.get(0));
            if (q == null) {
                throw new Exception("WHAT THE FUCK");
            }
            return q;
        }
        return null;
    }
}
