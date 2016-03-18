package com.noptech.stira.repository;

import com.noptech.stira.domain.QueueSource;
import com.noptech.stira.domain.enumeration.TicketSource;

import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * Spring Data JPA repository for the QueueSource entity.
 */
public interface QueueSourceRepository {

    QueueSource findOneByTicketSource(TicketSource source);

    QueueSource save(QueueSource source);

    QueueSource findOne(Long id);

    List<QueueSource> findAll();

    void delete(Long id);

}
