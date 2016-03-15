package com.noptech.stira.web.rest;

import com.noptech.stira.Application;
import com.noptech.stira.domain.QueuedForUpdate;
import com.noptech.stira.repository.QueuedForUpdateRepository;
import com.noptech.stira.service.QueuedForUpdateService;

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
 * Test class for the QueuedForUpdateResource REST controller.
 *
 * @see QueuedForUpdateResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class QueuedForUpdateResourceIntTest {

    
    private static final TicketSource DEFAULT_TICKET_SOURCE = TicketSource.STORM;
    private static final TicketSource UPDATED_TICKET_SOURCE = TicketSource.JIRA;

    private static final LocalDate DEFAULT_ADDED_TO_QUEUE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ADDED_TO_QUEUE = LocalDate.now(ZoneId.systemDefault());
    private static final String DEFAULT_TICKET_KEY = "AAAAA";
    private static final String UPDATED_TICKET_KEY = "BBBBB";

    @Inject
    private QueuedForUpdateRepository queuedForUpdateRepository;

    @Inject
    private QueuedForUpdateService queuedForUpdateService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restQueuedForUpdateMockMvc;

    private QueuedForUpdate queuedForUpdate;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        QueuedForUpdateResource queuedForUpdateResource = new QueuedForUpdateResource();
        ReflectionTestUtils.setField(queuedForUpdateResource, "queuedForUpdateService", queuedForUpdateService);
        this.restQueuedForUpdateMockMvc = MockMvcBuilders.standaloneSetup(queuedForUpdateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        queuedForUpdate = new QueuedForUpdate();
        queuedForUpdate.setTicketSource(DEFAULT_TICKET_SOURCE);
        queuedForUpdate.setAddedToQueue(DEFAULT_ADDED_TO_QUEUE);
        queuedForUpdate.setTicketKey(DEFAULT_TICKET_KEY);
    }

    @Test
    @Transactional
    public void createQueuedForUpdate() throws Exception {
        int databaseSizeBeforeCreate = queuedForUpdateRepository.findAll().size();

        // Create the QueuedForUpdate

        restQueuedForUpdateMockMvc.perform(post("/api/queuedForUpdates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(queuedForUpdate)))
                .andExpect(status().isCreated());

        // Validate the QueuedForUpdate in the database
        List<QueuedForUpdate> queuedForUpdates = queuedForUpdateRepository.findAll();
        assertThat(queuedForUpdates).hasSize(databaseSizeBeforeCreate + 1);
        QueuedForUpdate testQueuedForUpdate = queuedForUpdates.get(queuedForUpdates.size() - 1);
        assertThat(testQueuedForUpdate.getTicketSource()).isEqualTo(DEFAULT_TICKET_SOURCE);
        assertThat(testQueuedForUpdate.getAddedToQueue()).isEqualTo(DEFAULT_ADDED_TO_QUEUE);
        assertThat(testQueuedForUpdate.getTicketKey()).isEqualTo(DEFAULT_TICKET_KEY);
    }

    @Test
    @Transactional
    public void getAllQueuedForUpdates() throws Exception {
        // Initialize the database
        queuedForUpdateRepository.saveAndFlush(queuedForUpdate);

        // Get all the queuedForUpdates
        restQueuedForUpdateMockMvc.perform(get("/api/queuedForUpdates?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(queuedForUpdate.getId().intValue())))
                .andExpect(jsonPath("$.[*].ticketSource").value(hasItem(DEFAULT_TICKET_SOURCE.toString())))
                .andExpect(jsonPath("$.[*].addedToQueue").value(hasItem(DEFAULT_ADDED_TO_QUEUE.toString())))
                .andExpect(jsonPath("$.[*].ticketKey").value(hasItem(DEFAULT_TICKET_KEY.toString())));
    }

    @Test
    @Transactional
    public void getQueuedForUpdate() throws Exception {
        // Initialize the database
        queuedForUpdateRepository.saveAndFlush(queuedForUpdate);

        // Get the queuedForUpdate
        restQueuedForUpdateMockMvc.perform(get("/api/queuedForUpdates/{id}", queuedForUpdate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(queuedForUpdate.getId().intValue()))
            .andExpect(jsonPath("$.ticketSource").value(DEFAULT_TICKET_SOURCE.toString()))
            .andExpect(jsonPath("$.addedToQueue").value(DEFAULT_ADDED_TO_QUEUE.toString()))
            .andExpect(jsonPath("$.ticketKey").value(DEFAULT_TICKET_KEY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingQueuedForUpdate() throws Exception {
        // Get the queuedForUpdate
        restQueuedForUpdateMockMvc.perform(get("/api/queuedForUpdates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQueuedForUpdate() throws Exception {
        // Initialize the database
        queuedForUpdateRepository.saveAndFlush(queuedForUpdate);

		int databaseSizeBeforeUpdate = queuedForUpdateRepository.findAll().size();

        // Update the queuedForUpdate
        queuedForUpdate.setTicketSource(UPDATED_TICKET_SOURCE);
        queuedForUpdate.setAddedToQueue(UPDATED_ADDED_TO_QUEUE);
        queuedForUpdate.setTicketKey(UPDATED_TICKET_KEY);

        restQueuedForUpdateMockMvc.perform(put("/api/queuedForUpdates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(queuedForUpdate)))
                .andExpect(status().isOk());

        // Validate the QueuedForUpdate in the database
        List<QueuedForUpdate> queuedForUpdates = queuedForUpdateRepository.findAll();
        assertThat(queuedForUpdates).hasSize(databaseSizeBeforeUpdate);
        QueuedForUpdate testQueuedForUpdate = queuedForUpdates.get(queuedForUpdates.size() - 1);
        assertThat(testQueuedForUpdate.getTicketSource()).isEqualTo(UPDATED_TICKET_SOURCE);
        assertThat(testQueuedForUpdate.getAddedToQueue()).isEqualTo(UPDATED_ADDED_TO_QUEUE);
        assertThat(testQueuedForUpdate.getTicketKey()).isEqualTo(UPDATED_TICKET_KEY);
    }

    @Test
    @Transactional
    public void deleteQueuedForUpdate() throws Exception {
        // Initialize the database
        queuedForUpdateRepository.saveAndFlush(queuedForUpdate);

		int databaseSizeBeforeDelete = queuedForUpdateRepository.findAll().size();

        // Get the queuedForUpdate
        restQueuedForUpdateMockMvc.perform(delete("/api/queuedForUpdates/{id}", queuedForUpdate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<QueuedForUpdate> queuedForUpdates = queuedForUpdateRepository.findAll();
        assertThat(queuedForUpdates).hasSize(databaseSizeBeforeDelete - 1);
    }
}
