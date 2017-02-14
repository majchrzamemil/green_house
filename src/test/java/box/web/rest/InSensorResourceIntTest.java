package box.web.rest;

import box.GreenHouseApp;

import box.domain.InSensor;
import box.repository.InSensorRepository;

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
 * Test class for the InSensorResource REST controller.
 *
 * @see InSensorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenHouseApp.class)
public class InSensorResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_PIN_NUMBER = 1;
    private static final Integer UPDATED_PIN_NUMBER = 2;

    @Autowired
    private InSensorRepository inSensorRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private EntityManager em;

    private MockMvc restInSensorMockMvc;

    private InSensor inSensor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
            InSensorResource inSensorResource = new InSensorResource(inSensorRepository);
        this.restInSensorMockMvc = MockMvcBuilders.standaloneSetup(inSensorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InSensor createEntity(EntityManager em) {
        InSensor inSensor = new InSensor()
                .name(DEFAULT_NAME)
                .pinNumber(DEFAULT_PIN_NUMBER);
        return inSensor;
    }

    @Before
    public void initTest() {
        inSensor = createEntity(em);
    }

    @Test
    @Transactional
    public void createInSensor() throws Exception {
        int databaseSizeBeforeCreate = inSensorRepository.findAll().size();

        // Create the InSensor

        restInSensorMockMvc.perform(post("/api/in-sensors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(inSensor)))
            .andExpect(status().isCreated());

        // Validate the InSensor in the database
        List<InSensor> inSensorList = inSensorRepository.findAll();
        assertThat(inSensorList).hasSize(databaseSizeBeforeCreate + 1);
        InSensor testInSensor = inSensorList.get(inSensorList.size() - 1);
        assertThat(testInSensor.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInSensor.getPinNumber()).isEqualTo(DEFAULT_PIN_NUMBER);
    }

    @Test
    @Transactional
    public void createInSensorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = inSensorRepository.findAll().size();

        // Create the InSensor with an existing ID
        InSensor existingInSensor = new InSensor();
        existingInSensor.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInSensorMockMvc.perform(post("/api/in-sensors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingInSensor)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<InSensor> inSensorList = inSensorRepository.findAll();
        assertThat(inSensorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPinNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = inSensorRepository.findAll().size();
        // set the field null
        inSensor.setPinNumber(null);

        // Create the InSensor, which fails.

        restInSensorMockMvc.perform(post("/api/in-sensors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(inSensor)))
            .andExpect(status().isBadRequest());

        List<InSensor> inSensorList = inSensorRepository.findAll();
        assertThat(inSensorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllInSensors() throws Exception {
        // Initialize the database
        inSensorRepository.saveAndFlush(inSensor);

        // Get all the inSensorList
        restInSensorMockMvc.perform(get("/api/in-sensors?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inSensor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].pinNumber").value(hasItem(DEFAULT_PIN_NUMBER)));
    }

    @Test
    @Transactional
    public void getInSensor() throws Exception {
        // Initialize the database
        inSensorRepository.saveAndFlush(inSensor);

        // Get the inSensor
        restInSensorMockMvc.perform(get("/api/in-sensors/{id}", inSensor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(inSensor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.pinNumber").value(DEFAULT_PIN_NUMBER));
    }

    @Test
    @Transactional
    public void getNonExistingInSensor() throws Exception {
        // Get the inSensor
        restInSensorMockMvc.perform(get("/api/in-sensors/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInSensor() throws Exception {
        // Initialize the database
        inSensorRepository.saveAndFlush(inSensor);
        int databaseSizeBeforeUpdate = inSensorRepository.findAll().size();

        // Update the inSensor
        InSensor updatedInSensor = inSensorRepository.findOne(inSensor.getId());
        updatedInSensor
                .name(UPDATED_NAME)
                .pinNumber(UPDATED_PIN_NUMBER);

        restInSensorMockMvc.perform(put("/api/in-sensors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedInSensor)))
            .andExpect(status().isOk());

        // Validate the InSensor in the database
        List<InSensor> inSensorList = inSensorRepository.findAll();
        assertThat(inSensorList).hasSize(databaseSizeBeforeUpdate);
        InSensor testInSensor = inSensorList.get(inSensorList.size() - 1);
        assertThat(testInSensor.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInSensor.getPinNumber()).isEqualTo(UPDATED_PIN_NUMBER);
    }

    @Test
    @Transactional
    public void updateNonExistingInSensor() throws Exception {
        int databaseSizeBeforeUpdate = inSensorRepository.findAll().size();

        // Create the InSensor

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restInSensorMockMvc.perform(put("/api/in-sensors")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(inSensor)))
            .andExpect(status().isCreated());

        // Validate the InSensor in the database
        List<InSensor> inSensorList = inSensorRepository.findAll();
        assertThat(inSensorList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteInSensor() throws Exception {
        // Initialize the database
        inSensorRepository.saveAndFlush(inSensor);
        int databaseSizeBeforeDelete = inSensorRepository.findAll().size();

        // Get the inSensor
        restInSensorMockMvc.perform(delete("/api/in-sensors/{id}", inSensor.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<InSensor> inSensorList = inSensorRepository.findAll();
        assertThat(inSensorList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InSensor.class);
    }
}
