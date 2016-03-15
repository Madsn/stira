package com.noptech.stira.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.noptech.stira.domain.Sources;
import com.noptech.stira.repository.SourcesRepository;
import com.noptech.stira.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Sources.
 */
@RestController
@RequestMapping("/api")
public class SourcesResource {

    private final Logger log = LoggerFactory.getLogger(SourcesResource.class);
        
    @Inject
    private SourcesRepository sourcesRepository;
    
    /**
     * POST  /sourcess -> Create a new sources.
     */
    @RequestMapping(value = "/sourcess",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sources> createSources(@RequestBody Sources sources) throws URISyntaxException {
        log.debug("REST request to save Sources : {}", sources);
        if (sources.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sources", "idexists", "A new sources cannot already have an ID")).body(null);
        }
        Sources result = sourcesRepository.save(sources);
        return ResponseEntity.created(new URI("/api/sourcess/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sources", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sourcess -> Updates an existing sources.
     */
    @RequestMapping(value = "/sourcess",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sources> updateSources(@RequestBody Sources sources) throws URISyntaxException {
        log.debug("REST request to update Sources : {}", sources);
        if (sources.getId() == null) {
            return createSources(sources);
        }
        Sources result = sourcesRepository.save(sources);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sources", sources.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sourcess -> get all the sourcess.
     */
    @RequestMapping(value = "/sourcess",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Sources> getAllSourcess() {
        log.debug("REST request to get all Sourcess");
        return sourcesRepository.findAll();
            }

    /**
     * GET  /sourcess/:id -> get the "id" sources.
     */
    @RequestMapping(value = "/sourcess/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sources> getSources(@PathVariable Long id) {
        log.debug("REST request to get Sources : {}", id);
        Sources sources = sourcesRepository.findOne(id);
        return Optional.ofNullable(sources)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sourcess/:id -> delete the "id" sources.
     */
    @RequestMapping(value = "/sourcess/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSources(@PathVariable Long id) {
        log.debug("REST request to delete Sources : {}", id);
        sourcesRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sources", id.toString())).build();
    }
}
