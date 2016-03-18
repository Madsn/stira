package com.noptech.stira.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.noptech.stira.domain.QueueSource;
import com.noptech.stira.repository.QueueSourceRepository;
import com.noptech.stira.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing QueueSource.
 */
@RestController
@RequestMapping("/api")
public class QueueSourceResource {

    private final Logger log = LoggerFactory.getLogger(QueueSourceResource.class);

    @Inject
    private QueueSourceRepository queueSourceRepository;

    /**
     * POST  /queueSources -> Create a new queueSource.
     */
    @RequestMapping(value = "/queueSources",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QueueSource> createQueueSource(@RequestBody QueueSource queueSource) throws URISyntaxException {
        log.debug("REST request to save QueueSource : {}", queueSource);
        if (queueSource.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("queueSource", "idexists", "A new queueSource cannot already have an ID")).body(null);
        }
        QueueSource result = queueSourceRepository.save(queueSource);
        return ResponseEntity.created(new URI("/api/queueSources/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("queueSource", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /queueSources -> Updates an existing queueSource.
     */
    @RequestMapping(value = "/queueSources",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QueueSource> updateQueueSource(@RequestBody QueueSource queueSource) throws URISyntaxException {
        log.debug("REST request to update QueueSource : {}", queueSource);
        if (queueSource.getId() == null) {
            return createQueueSource(queueSource);
        }
        QueueSource result = queueSourceRepository.save(queueSource);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("queueSource", queueSource.getId().toString()))
            .body(result);
    }

    /**
     * GET  /queueSources -> get all the queueSources.
     */
    @RequestMapping(value = "/queueSources",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<QueueSource> getAllQueueSources() {
        log.debug("REST request to get all QueueSources");
        return queueSourceRepository.findAll();
            }

    /**
     * GET  /queueSources/:id -> get the "id" queueSource.
     */
    @RequestMapping(value = "/queueSources/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QueueSource> getQueueSource(@PathVariable Long id) {
        log.debug("REST request to get QueueSource : {}", id);
        QueueSource queueSource = queueSourceRepository.findOne(id);
        return Optional.ofNullable(queueSource)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /queueSources/:id -> delete the "id" queueSource.
     */
    @RequestMapping(value = "/queueSources/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteQueueSource(@PathVariable Long id) {
        log.debug("REST request to delete QueueSource : {}", id);
        queueSourceRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("queueSource", id.toString())).build();
    }
}
