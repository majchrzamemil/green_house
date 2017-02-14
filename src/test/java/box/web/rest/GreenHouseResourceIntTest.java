package box.web.rest;

import box.GreenHouseApp;

import box.domain.GreenHouse;
import box.repository.GreenHouseRepository;

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
 * Test class for the GreenHouseResource REST controller.
 *
 * @see GreenHouseResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenHouseApp.class)
public class GreenHouseResourceIntTest {

    @Autowired
    private GreenHouseRepository greenHouseRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private EntityManager em;

    private MockMvc restGreenHouseMockMvc;

    private GreenHouse greenHouse;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
            GreenHouseResource greenHouseResource = new GreenHouseResource(greenHouseRepository);
        this.restGreenHouseMockMvc = MockMvcBuilders.standaloneSetup(greenHouseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GreenHouse createEntity(EntityManager em) {
        GreenHouse greenHouse = new GreenHouse();
        return greenHouse;
    }

    @Before
    public void initTest() {
        greenHouse = createEntity(em);
    }

    @Test
    @Transactional
    public void createGreenHouse() throws Exception {
        int databaseSizeBeforeCreate = greenHouseRepository.findAll().size();

        // Create the GreenHouse

        restGreenHouseMockMvc.perform(post("/api/green-houses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greenHouse)))
            .andExpect(status().isCreated());

        // Validate the GreenHouse in the database
        List<GreenHouse> greenHouseList = greenHouseRepository.findAll();
        assertThat(greenHouseList).hasSize(databaseSizeBeforeCreate + 1);
        GreenHouse testGreenHouse = greenHouseList.get(greenHouseList.size() - 1);
    }

    @Test
    @Transactional
    public void createGreenHouseWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = greenHouseRepository.findAll().size();

        // Create the GreenHouse with an existing ID
        GreenHouse existingGreenHouse = new GreenHouse();
        existingGreenHouse.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGreenHouseMockMvc.perform(post("/api/green-houses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingGreenHouse)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<GreenHouse> greenHouseList = greenHouseRepository.findAll();
        assertThat(greenHouseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllGreenHouses() throws Exception {
        // Initialize the database
        greenHouseRepository.saveAndFlush(greenHouse);

        // Get all the greenHouseList
        restGreenHouseMockMvc.perform(get("/api/green-houses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(greenHouse.getId().intValue())));
    }

    @Test
    @Transactional
    public void getGreenHouse() throws Exception {
        // Initialize the database
        greenHouseRepository.saveAndFlush(greenHouse);

        // Get the greenHouse
        restGreenHouseMockMvc.perform(get("/api/green-houses/{id}", greenHouse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(greenHouse.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingGreenHouse() throws Exception {
        // Get the greenHouse
        restGreenHouseMockMvc.perform(get("/api/green-houses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGreenHouse() throws Exception {
        // Initialize the database
        greenHouseRepository.saveAndFlush(greenHouse);
        int databaseSizeBeforeUpdate = greenHouseRepository.findAll().size();

        // Update the greenHouse
        GreenHouse updatedGreenHouse = greenHouseRepository.findOne(greenHouse.getId());

        restGreenHouseMockMvc.perform(put("/api/green-houses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedGreenHouse)))
            .andExpect(status().isOk());

        // Validate the GreenHouse in the database
        List<GreenHouse> greenHouseList = greenHouseRepository.findAll();
        assertThat(greenHouseList).hasSize(databaseSizeBeforeUpdate);
        GreenHouse testGreenHouse = greenHouseList.get(greenHouseList.size() - 1);
    }

    @Test
    @Transactional
    public void updateNonExistingGreenHouse() throws Exception {
        int databaseSizeBeforeUpdate = greenHouseRepository.findAll().size();

        // Create the GreenHouse

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restGreenHouseMockMvc.perform(put("/api/green-houses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(greenHouse)))
            .andExpect(status().isCreated());

        // Validate the GreenHouse in the database
        List<GreenHouse> greenHouseList = greenHouseRepository.findAll();
        assertThat(greenHouseList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteGreenHouse() throws Exception {
        // Initialize the database
        greenHouseRepository.saveAndFlush(greenHouse);
        int databaseSizeBeforeDelete = greenHouseRepository.findAll().size();

        // Get the greenHouse
        restGreenHouseMockMvc.perform(delete("/api/green-houses/{id}", greenHouse.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<GreenHouse> greenHouseList = greenHouseRepository.findAll();
        assertThat(greenHouseList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GreenHouse.class);
    }
}
