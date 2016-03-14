package com.noptech.stira.web.rest;

import com.noptech.stira.Application;
import com.noptech.stira.domain.Ticket;
import com.noptech.stira.repository.TicketRepository;

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


/**
 * Test class for the TicketResource REST controller.
 *
 * @see TicketResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TicketResourceIntTest {


    private static final Long DEFAULT_STORM_KEY = 1L;
    private static final Long UPDATED_STORM_KEY = 2L;
    private static final String DEFAULT_JIRA_KEY = "AAAAA";
    private static final String UPDATED_JIRA_KEY = "BBBBB";
    private static final String DEFAULT_JIRA_TITLE = "AAAAA";
    private static final String UPDATED_JIRA_TITLE = "BBBBB";
    private static final String DEFAULT_STORM_TITLE = "AAAAA";
    private static final String UPDATED_STORM_TITLE = "BBBBB";

    private static final LocalDate DEFAULT_JIRA_LAST_UPDATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_JIRA_LAST_UPDATED = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private TicketRepository ticketRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTicketMockMvc;

    private Ticket ticket;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TicketResource ticketResource = new TicketResource();
        ReflectionTestUtils.setField(ticketResource, "ticketRepository", ticketRepository);
        this.restTicketMockMvc = MockMvcBuilders.standaloneSetup(ticketResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        ticket = new Ticket();
        ticket.setStormKey(DEFAULT_STORM_KEY);
        ticket.setJiraKey(DEFAULT_JIRA_KEY);
        ticket.setJiraTitle(DEFAULT_JIRA_TITLE);
        ticket.setStormTitle(DEFAULT_STORM_TITLE);
        ticket.setJiraLastUpdated(DEFAULT_JIRA_LAST_UPDATED);
    }

    @Test
    @Transactional
    public void createTicket() throws Exception {
        int databaseSizeBeforeCreate = ticketRepository.findAll().size();

        // Create the Ticket

        restTicketMockMvc.perform(post("/api/tickets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ticket)))
                .andExpect(status().isCreated());

        // Validate the Ticket in the database
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(databaseSizeBeforeCreate + 1);
        Ticket testTicket = tickets.get(tickets.size() - 1);
        assertThat(testTicket.getStormKey()).isEqualTo(DEFAULT_STORM_KEY);
        assertThat(testTicket.getJiraKey()).isEqualTo(DEFAULT_JIRA_KEY);
        assertThat(testTicket.getJiraTitle()).isEqualTo(DEFAULT_JIRA_TITLE);
        assertThat(testTicket.getStormTitle()).isEqualTo(DEFAULT_STORM_TITLE);
        assertThat(testTicket.getJiraLastUpdated()).isEqualTo(DEFAULT_JIRA_LAST_UPDATED);
    }

    @Test
    @Transactional
    public void getAllTickets() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get all the tickets
        restTicketMockMvc.perform(get("/api/tickets?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(ticket.getId().intValue())))
                .andExpect(jsonPath("$.[*].stormKey").value(hasItem(DEFAULT_STORM_KEY.intValue())))
                .andExpect(jsonPath("$.[*].jiraKey").value(hasItem(DEFAULT_JIRA_KEY.toString())))
                .andExpect(jsonPath("$.[*].jiraTitle").value(hasItem(DEFAULT_JIRA_TITLE.toString())))
                .andExpect(jsonPath("$.[*].stormTitle").value(hasItem(DEFAULT_STORM_TITLE.toString())))
                .andExpect(jsonPath("$.[*].jiraLastUpdated").value(hasItem(DEFAULT_JIRA_LAST_UPDATED.toString())));
    }

    @Test
    @Transactional
    public void getTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", ticket.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ticket.getId().intValue()))
            .andExpect(jsonPath("$.stormKey").value(DEFAULT_STORM_KEY.intValue()))
            .andExpect(jsonPath("$.jiraKey").value(DEFAULT_JIRA_KEY.toString()))
            .andExpect(jsonPath("$.jiraTitle").value(DEFAULT_JIRA_TITLE.toString()))
            .andExpect(jsonPath("$.stormTitle").value(DEFAULT_STORM_TITLE.toString()))
            .andExpect(jsonPath("$.jiraLastUpdated").value(DEFAULT_JIRA_LAST_UPDATED.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTicket() throws Exception {
        // Get the ticket
        restTicketMockMvc.perform(get("/api/tickets/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

		int databaseSizeBeforeUpdate = ticketRepository.findAll().size();

        // Update the ticket
        ticket.setStormKey(UPDATED_STORM_KEY);
        ticket.setJiraKey(UPDATED_JIRA_KEY);
        ticket.setJiraTitle(UPDATED_JIRA_TITLE);
        ticket.setStormTitle(UPDATED_STORM_TITLE);
        ticket.setJiraLastUpdated(UPDATED_JIRA_LAST_UPDATED);

        restTicketMockMvc.perform(put("/api/tickets")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ticket)))
                .andExpect(status().isOk());

        // Validate the Ticket in the database
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(databaseSizeBeforeUpdate);
        Ticket testTicket = tickets.get(tickets.size() - 1);
        assertThat(testTicket.getStormKey()).isEqualTo(UPDATED_STORM_KEY);
        assertThat(testTicket.getJiraKey()).isEqualTo(UPDATED_JIRA_KEY);
        assertThat(testTicket.getJiraTitle()).isEqualTo(UPDATED_JIRA_TITLE);
        assertThat(testTicket.getStormTitle()).isEqualTo(UPDATED_STORM_TITLE);
        assertThat(testTicket.getJiraLastUpdated()).isEqualTo(UPDATED_JIRA_LAST_UPDATED);
    }

    @Test
    @Transactional
    public void deleteTicket() throws Exception {
        // Initialize the database
        ticketRepository.saveAndFlush(ticket);

		int databaseSizeBeforeDelete = ticketRepository.findAll().size();

        // Get the ticket
        restTicketMockMvc.perform(delete("/api/tickets/{id}", ticket.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets).hasSize(databaseSizeBeforeDelete - 1);
    }
}
