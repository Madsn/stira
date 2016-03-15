package com.noptech.stira.repository;

import com.noptech.stira.domain.QueuedForUpdate;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the QueuedForUpdate entity.
 */
public interface QueuedForUpdateRepository extends JpaRepository<QueuedForUpdate,Long> {

}
