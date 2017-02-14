package box.web.rest;

import box.GreenHouseApp;

import box.domain.GreenHouseManager;
import box.repository.GreenHouseManagerRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the GreenHouseManagerResource REST controller.
 *
 * @see GreenHouseManagerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenHouseApp.class)
public class GreenHouseManagerResourceIntTest {

    @Autowired
    private GreenHouseManagerRepository greenHouseManagerRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private EntityManager em;

    private MockMvc restGreenHouseManagerMockMvc;

    private GreenHouseManager greenHouseManager;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
            GreenHouseManagerResource greenHouseManagerResource = new GreenHouseManagerResource(greenHouseManagerRepository);
        this.restGreenHouseManagerMockMvc = MockMvcBuilders.standaloneSetup(greenHouseManagerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GreenHouseManager createEntity(EntityManager em) {
        GreenHouseManager greenHouseManager = new GreenHouseManager();
        return greenHouseManager;
    }

    @Before
    public void initTest() {
        greenHouseManager = createEntity(em);
    }

    @Test
    @Transactional
    public void createGreenHouseManager() throws Exception {
        int databaseSizeBeforeCreate = greenHouseManagerRepository.findAll().size();

        // Create the GreenHouseManager

        restGreenHouseManagerMockMvc.perform(post("/api/green-house-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greenHouseManager)))
            .andExpect(status().isCreated());

        // Validate the GreenHouseManager in the database
        List<GreenHouseManager> greenHouseManagerList = greenHouseManagerRepository.findAll();
        assertThat(greenHouseManagerList).hasSize(databaseSizeBeforeCreate + 1);
        GreenHouseManager testGreenHouseManager = greenHouseManagerList.get(greenHouseManagerList.size() - 1);
    }

    @Test
    @Transactional
    public void createGreenHouseManagerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = greenHouseManagerRepository.findAll().size();

        // Create the GreenHouseManager with an existing ID
        GreenHouseManager existingGreenHouseManager = new GreenHouseManager();
        existingGreenHouseManager.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGreenHouseManagerMockMvc.perform(post("/api/green-house-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingGreenHouseManager)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<GreenHouseManager> greenHouseManagerList = greenHouseManagerRepository.findAll();
        assertThat(greenHouseManagerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllGreenHouseManagers() throws Exception {
        // Initialize the database
        greenHouseManagerRepository.saveAndFlush(greenHouseManager);

        // Get all the greenHouseManagerList
        restGreenHouseManagerMockMvc.perform(get("/api/green-house-managers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(greenHouseManager.getId().intValue())));
    }

    @Test
    @Transactional
    public void getGreenHouseManager() throws Exception {
        // Initialize the database
        greenHouseManagerRepository.saveAndFlush(greenHouseManager);

        // Get the greenHouseManager
        restGreenHouseManagerMockMvc.perform(get("/api/green-house-managers/{id}", greenHouseManager.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(greenHouseManager.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingGreenHouseManager() throws Exception {
        // Get the greenHouseManager
        restGreenHouseManagerMockMvc.perform(get("/api/green-house-managers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGreenHouseManager() throws Exception {
        // Initialize the database
        greenHouseManagerRepository.saveAndFlush(greenHouseManager);
        int databaseSizeBeforeUpdate = greenHouseManagerRepository.findAll().size();

        // Update the greenHouseManager
        GreenHouseManager updatedGreenHouseManager = greenHouseManagerRepository.findOne(greenHouseManager.getId());

        restGreenHouseManagerMockMvc.perform(put("/api/green-house-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGreenHouseManager)))
            .andExpect(status().isOk());

        // Validate the GreenHouseManager in the database
        List<GreenHouseManager> greenHouseManagerList = greenHouseManagerRepository.findAll();
        assertThat(greenHouseManagerList).hasSize(databaseSizeBeforeUpdate);
        GreenHouseManager testGreenHouseManager = greenHouseManagerList.get(greenHouseManagerList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingGreenHouseManager() throws Exception {
        int databaseSizeBeforeUpdate = greenHouseManagerRepository.findAll().size();

        // Create the GreenHouseManager

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restGreenHouseManagerMockMvc.perform(put("/api/green-house-managers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greenHouseManager)))
            .andExpect(status().isCreated());

        // Validate the GreenHouseManager in the database
        List<GreenHouseManager> greenHouseManagerList = greenHouseManagerRepository.findAll();
        assertThat(greenHouseManagerList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteGreenHouseManager() throws Exception {
        // Initialize the database
        greenHouseManagerRepository.saveAndFlush(greenHouseManager);
        int databaseSizeBeforeDelete = greenHouseManagerRepository.findAll().size();

        // Get the greenHouseManager
        restGreenHouseManagerMockMvc.perform(delete("/api/green-house-managers/{id}", greenHouseManager.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<GreenHouseManager> greenHouseManagerList = greenHouseManagerRepository.findAll();
        assertThat(greenHouseManagerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GreenHouseManager.class);
    }
}
