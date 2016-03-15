package com.noptech.stira.web.rest;

import com.noptech.stira.Application;
import com.noptech.stira.domain.Sources;
import com.noptech.stira.repository.SourcesRepository;

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
 * Test class for the SourcesResource REST controller.
 *
 * @see SourcesResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SourcesResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final LocalDate DEFAULT_SYNCED_TO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SYNCED_TO = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private SourcesRepository sourcesRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSourcesMockMvc;

    private Sources sources;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SourcesResource sourcesResource = new SourcesResource();
        ReflectionTestUtils.setField(sourcesResource, "sourcesRepository", sourcesRepository);
        this.restSourcesMockMvc = MockMvcBuilders.standaloneSetup(sourcesResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        sources = new Sources();
        sources.setName(DEFAULT_NAME);
        sources.setSyncedTo(DEFAULT_SYNCED_TO);
    }

    @Test
    @Transactional
    public void createSources() throws Exception {
        int databaseSizeBeforeCreate = sourcesRepository.findAll().size();

        // Create the Sources

        restSourcesMockMvc.perform(post("/api/sourcess")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sources)))
                .andExpect(status().isCreated());

        // Validate the Sources in the database
        List<Sources> sourcess = sourcesRepository.findAll();
        assertThat(sourcess).hasSize(databaseSizeBeforeCreate + 1);
        Sources testSources = sourcess.get(sourcess.size() - 1);
        assertThat(testSources.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSources.getSyncedTo()).isEqualTo(DEFAULT_SYNCED_TO);
    }

    @Test
    @Transactional
    public void getAllSourcess() throws Exception {
        // Initialize the database
        sourcesRepository.saveAndFlush(sources);

        // Get all the sourcess
        restSourcesMockMvc.perform(get("/api/sourcess?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sources.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].syncedTo").value(hasItem(DEFAULT_SYNCED_TO.toString())));
    }

    @Test
    @Transactional
    public void getSources() throws Exception {
        // Initialize the database
        sourcesRepository.saveAndFlush(sources);

        // Get the sources
        restSourcesMockMvc.perform(get("/api/sourcess/{id}", sources.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(sources.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.syncedTo").value(DEFAULT_SYNCED_TO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSources() throws Exception {
        // Get the sources
        restSourcesMockMvc.perform(get("/api/sourcess/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSources() throws Exception {
        // Initialize the database
        sourcesRepository.saveAndFlush(sources);

		int databaseSizeBeforeUpdate = sourcesRepository.findAll().size();

        // Update the sources
        sources.setName(UPDATED_NAME);
        sources.setSyncedTo(UPDATED_SYNCED_TO);

        restSourcesMockMvc.perform(put("/api/sourcess")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sources)))
                .andExpect(status().isOk());

        // Validate the Sources in the database
        List<Sources> sourcess = sourcesRepository.findAll();
        assertThat(sourcess).hasSize(databaseSizeBeforeUpdate);
        Sources testSources = sourcess.get(sourcess.size() - 1);
        assertThat(testSources.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSources.getSyncedTo()).isEqualTo(UPDATED_SYNCED_TO);
    }

    @Test
    @Transactional
    public void deleteSources() throws Exception {
        // Initialize the database
        sourcesRepository.saveAndFlush(sources);

		int databaseSizeBeforeDelete = sourcesRepository.findAll().size();

        // Get the sources
        restSourcesMockMvc.perform(delete("/api/sourcess/{id}", sources.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Sources> sourcess = sourcesRepository.findAll();
        assertThat(sourcess).hasSize(databaseSizeBeforeDelete - 1);
    }
}
