package com.noptech.stira.repository;

import com.noptech.stira.domain.Sources;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Sources entity.
 */
public interface SourcesRepository extends JpaRepository<Sources,Long> {

}
