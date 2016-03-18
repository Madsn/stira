package com.noptech.stira.service;

import com.noptech.stira.domain.QueuedForUpdate;
import com.noptech.stira.domain.Ticket;
import com.noptech.stira.domain.enumeration.TicketSource;
import com.noptech.stira.repository.QueuedForUpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing QueuedForUpdate.
 */
@Service
@Transactional
public class QueuedForUpdateService {

    private final Logger log = LoggerFactory.getLogger(QueuedForUpdateService.class);

    @Inject
    private QueuedForUpdateRepository queuedForUpdateRepository;

    public void addToQueue(List<Ticket> tickets) {
        for (Ticket t : tickets) {
            QueuedForUpdate q = new QueuedForUpdate();
            if (t.getStormKey() != null) {
                q.setTicketSource(TicketSource.STORM);
                q.setAddedToQueue(t.getStormLastUpdated());
                // TODO - refactor ticket entity to use common lastupdated and ticketKey fields
                q.setTicketKey(String.valueOf(t.getStormKey()));
            } else if (t.getJiraKey() != null) {
                q.setTicketSource(TicketSource.JIRA);
                q.setAddedToQueue(t.getJiraLastUpdated());
                q.setTicketKey(t.getJiraKey());
            }
            log.debug("Saving queuedForUpdate entity: " + q.toString());
            QueuedForUpdate oldQ = queuedForUpdateRepository.findByTicketKey(q.getTicketKey());
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
     * @return the persisted entity
     */
    public QueuedForUpdate save(QueuedForUpdate queuedForUpdate) {
        log.debug("Request to save QueuedForUpdate : {}", queuedForUpdate);
        QueuedForUpdate result = queuedForUpdateRepository.save(queuedForUpdate);
        return result;
    }

    /**
     *  get all the queuedForUpdates.
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<QueuedForUpdate> findAll() {
        log.debug("Request to get all QueuedForUpdates");
        List<QueuedForUpdate> result = queuedForUpdateRepository.findAll();
        return result;
    }

    /**
     *  get one queuedForUpdate by id.
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public QueuedForUpdate findOne(Long id) {
        log.debug("Request to get QueuedForUpdate : {}", id);
        QueuedForUpdate queuedForUpdate = queuedForUpdateRepository.findOne(id);
        return queuedForUpdate;
    }

    /**
     *  delete the  queuedForUpdate by id.
     */
    public void delete(Long id) {
        log.debug("Request to delete QueuedForUpdate : {}", id);
        queuedForUpdateRepository.delete(id);
    }

    public QueuedForUpdate getNext(TicketSource ticketSource) {
        List<QueuedForUpdate> updates = queuedForUpdateRepository.getOldest(ticketSource);
        if (updates != null && updates.size() > 0) {
            return updates.get(0);
        }
        return null;
    }
}
