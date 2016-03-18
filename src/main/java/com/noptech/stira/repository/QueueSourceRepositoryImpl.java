package com.noptech.stira.repository;

import com.noptech.stira.domain.QueueSource;
import com.noptech.stira.domain.enumeration.TicketSource;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class QueueSourceRepositoryImpl implements QueueSourceRepository {

    @PersistenceContext
    private EntityManager em;

    public QueueSource findOneByTicketSource(TicketSource source) {
        TypedQuery<QueueSource> query = em.createNamedQuery(QueueSource.FIND_BY_TICKET_SOURCE, QueueSource.class);
        query.setParameter("ticketSource", source);
        query.setMaxResults(1);
        return query.getSingleResult();
    }

    @Override
    public QueueSource save(QueueSource source) {
        return em.merge(source);
    }

    @Override
    public QueueSource findOne(Long id) {
        return em.find(QueueSource.class, id);
    }

    @Override
    public List<QueueSource> findAll() {
        TypedQuery<QueueSource> q = em.createNamedQuery(QueueSource.FIND_ALL, QueueSource.class);
        return q.getResultList();
    }

    @Override
    public void delete(Long id) {
        em.remove(em.find(QueueSource.class, id));
    }

}
