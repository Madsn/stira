package com.noptech.stira.web.rest;

import com.noptech.stira.Application;
import com.noptech.stira.domain.QueueSource;
import com.noptech.stira.repository.QueueSourceRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.noptech.stira.domain.enumeration.TicketSource;

/**
 * Test class for the QueueSourceResource REST controller.
 *
 * @see QueueSourceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class QueueSourceResourceIntTest {

    
    private static final TicketSource DEFAULT_TICKET_SOURCE = TicketSource.STORM;
    private static final TicketSource UPDATED_TICKET_SOURCE = TicketSource.JIRA;

    private static final LocalDate DEFAULT_LAST_ADDED_TICKET = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_ADDED_TICKET = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private QueueSourceRepository queueSourceRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restQueueSourceMockMvc;

    private QueueSource queueSource;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        QueueSourceResource queueSourceResource = new QueueSourceResource();
        ReflectionTestUtils.setField(queueSourceResource, "queueSourceRepository", queueSourceRepository);
        this.restQueueSourceMockMvc = MockMvcBuilders.standaloneSetup(queueSourceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        queueSource = new QueueSource();
        queueSource.setTicketSource(DEFAULT_TICKET_SOURCE);
        queueSource.setLastAddedTicket(DEFAULT_LAST_ADDED_TICKET);
    }

    @Test
    @Transactional
    public void createQueueSource() throws Exception {
        int databaseSizeBeforeCreate = queueSourceRepository.findAll().size();

        // Create the QueueSource

        restQueueSourceMockMvc.perform(post("/api/queueSources")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(queueSource)))
                .andExpect(status().isCreated());

        // Validate the QueueSource in the database
        List<QueueSource> queueSources = queueSourceRepository.findAll();
        assertThat(queueSources).hasSize(databaseSizeBeforeCreate + 1);
        QueueSource testQueueSource = queueSources.get(queueSources.size() - 1);
        assertThat(testQueueSource.getTicketSource()).isEqualTo(DEFAULT_TICKET_SOURCE);
        assertThat(testQueueSource.getLastAddedTicket()).isEqualTo(DEFAULT_LAST_ADDED_TICKET);
    }

    @Test
    @Transactional
    public void getAllQueueSources() throws Exception {
        // Initialize the database
        queueSourceRepository.saveAndFlush(queueSource);

        // Get all the queueSources
        restQueueSourceMockMvc.perform(get("/api/queueSources?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(queueSource.getId().intValue())))
                .andExpect(jsonPath("$.[*].ticketSource").value(hasItem(DEFAULT_TICKET_SOURCE.toString())))
                .andExpect(jsonPath("$.[*].lastAddedTicket").value(hasItem(DEFAULT_LAST_ADDED_TICKET.toString())));
    }

    @Test
    @Transactional
    public void getQueueSource() throws Exception {
        // Initialize the database
        queueSourceRepository.saveAndFlush(queueSource);

        // Get the queueSource
        restQueueSourceMockMvc.perform(get("/api/queueSources/{id}", queueSource.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(queueSource.getId().intValue()))
            .andExpect(jsonPath("$.ticketSource").value(DEFAULT_TICKET_SOURCE.toString()))
            .andExpect(jsonPath("$.lastAddedTicket").value(DEFAULT_LAST_ADDED_TICKET.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingQueueSource() throws Exception {
        // Get the queueSource
        restQueueSourceMockMvc.perform(get("/api/queueSources/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQueueSource() throws Exception {
        // Initialize the database
        queueSourceRepository.saveAndFlush(queueSource);

		int databaseSizeBeforeUpdate = queueSourceRepository.findAll().size();

        // Update the queueSource
        queueSource.setTicketSource(UPDATED_TICKET_SOURCE);
        queueSource.setLastAddedTicket(UPDATED_LAST_ADDED_TICKET);

        restQueueSourceMockMvc.perform(put("/api/queueSources")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(queueSource)))
                .andExpect(status().isOk());

        // Validate the QueueSource in the database
        List<QueueSource> queueSources = queueSourceRepository.findAll();
        assertThat(queueSources).hasSize(databaseSizeBeforeUpdate);
        QueueSource testQueueSource = queueSources.get(queueSources.size() - 1);
        assertThat(testQueueSource.getTicketSource()).isEqualTo(UPDATED_TICKET_SOURCE);
        assertThat(testQueueSource.getLastAddedTicket()).isEqualTo(UPDATED_LAST_ADDED_TICKET);
    }

    @Test
    @Transactional
    public void deleteQueueSource() throws Exception {
        // Initialize the database
        queueSourceRepository.saveAndFlush(queueSource);

		int databaseSizeBeforeDelete = queueSourceRepository.findAll().size();

        // Get the queueSource
        restQueueSourceMockMvc.perform(delete("/api/queueSources/{id}", queueSource.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<QueueSource> queueSources = queueSourceRepository.findAll();
        assertThat(queueSources).hasSize(databaseSizeBeforeDelete - 1);
    }
}
