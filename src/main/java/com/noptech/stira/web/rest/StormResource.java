package com.noptech.stira.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.noptech.stira.service.StormService;
import com.noptech.stira.web.rest.dto.StormStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping("/api")
public class StormResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    private StormService stormService;

    @RequestMapping(value = "/storm/{ticketIdParam:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public StormStatusDTO getStatus(@PathVariable Long ticketIdParam) {
        StormStatusDTO stormStatus = stormService.getStatus(ticketIdParam);
        return stormStatus;
    }
}
