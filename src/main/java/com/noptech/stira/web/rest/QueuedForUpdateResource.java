package com.noptech.stira.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.noptech.stira.domain.QueuedForUpdate;
import com.noptech.stira.service.QueuedForUpdateService;
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
 * REST controller for managing QueuedForUpdate.
 */
@RestController
@RequestMapping("/api")
public class QueuedForUpdateResource {

    private final Logger log = LoggerFactory.getLogger(QueuedForUpdateResource.class);
        
    @Inject
    private QueuedForUpdateService queuedForUpdateService;
    
    /**
     * POST  /queuedForUpdates -> Create a new queuedForUpdate.
     */
    @RequestMapping(value = "/queuedForUpdates",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QueuedForUpdate> createQueuedForUpdate(@RequestBody QueuedForUpdate queuedForUpdate) throws URISyntaxException {
        log.debug("REST request to save QueuedForUpdate : {}", queuedForUpdate);
        if (queuedForUpdate.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("queuedForUpdate", "idexists", "A new queuedForUpdate cannot already have an ID")).body(null);
        }
        QueuedForUpdate result = queuedForUpdateService.save(queuedForUpdate);
        return ResponseEntity.created(new URI("/api/queuedForUpdates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("queuedForUpdate", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /queuedForUpdates -> Updates an existing queuedForUpdate.
     */
    @RequestMapping(value = "/queuedForUpdates",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QueuedForUpdate> updateQueuedForUpdate(@RequestBody QueuedForUpdate queuedForUpdate) throws URISyntaxException {
        log.debug("REST request to update QueuedForUpdate : {}", queuedForUpdate);
        if (queuedForUpdate.getId() == null) {
            return createQueuedForUpdate(queuedForUpdate);
        }
        QueuedForUpdate result = queuedForUpdateService.save(queuedForUpdate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("queuedForUpdate", queuedForUpdate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /queuedForUpdates -> get all the queuedForUpdates.
     */
    @RequestMapping(value = "/queuedForUpdates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<QueuedForUpdate> getAllQueuedForUpdates() {
        log.debug("REST request to get all QueuedForUpdates");
        return queuedForUpdateService.findAll();
            }

    /**
     * GET  /queuedForUpdates/:id -> get the "id" queuedForUpdate.
     */
    @RequestMapping(value = "/queuedForUpdates/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QueuedForUpdate> getQueuedForUpdate(@PathVariable Long id) {
        log.debug("REST request to get QueuedForUpdate : {}", id);
        QueuedForUpdate queuedForUpdate = queuedForUpdateService.findOne(id);
        return Optional.ofNullable(queuedForUpdate)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /queuedForUpdates/:id -> delete the "id" queuedForUpdate.
     */
    @RequestMapping(value = "/queuedForUpdates/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteQueuedForUpdate(@PathVariable Long id) {
        log.debug("REST request to delete QueuedForUpdate : {}", id);
        queuedForUpdateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("queuedForUpdate", id.toString())).build();
    }
}
