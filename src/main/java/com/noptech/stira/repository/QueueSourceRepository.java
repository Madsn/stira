package com.noptech.stira.repository;

import com.noptech.stira.domain.QueueSource;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the QueueSource entity.
 */
public interface QueueSourceRepository extends JpaRepository<QueueSource,Long> {

}
