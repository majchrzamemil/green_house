package box.web.rest;

import box.GreenHouseApp;

import box.domain.OutSwitch;
import box.repository.OutSwitchRepository;

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
 * Test class for the OutSwitchResource REST controller.
 *
 * @see OutSwitchResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenHouseApp.class)
public class OutSwitchResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_PIN_NUMBER = 1;
    private static final Integer UPDATED_PIN_NUMBER = 2;

    @Autowired
    private OutSwitchRepository outSwitchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private EntityManager em;

    private MockMvc restOutSwitchMockMvc;

    private OutSwitch outSwitch;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
            OutSwitchResource outSwitchResource = new OutSwitchResource(outSwitchRepository);
        this.restOutSwitchMockMvc = MockMvcBuilders.standaloneSetup(outSwitchResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OutSwitch createEntity(EntityManager em) {
        OutSwitch outSwitch = new OutSwitch()
                .name(DEFAULT_NAME)
                .pinNumber(DEFAULT_PIN_NUMBER);
        return outSwitch;
    }

    @Before
    public void initTest() {
        outSwitch = createEntity(em);
    }

    @Test
    @Transactional
    public void createOutSwitch() throws Exception {
        int databaseSizeBeforeCreate = outSwitchRepository.findAll().size();

        // Create the OutSwitch

        restOutSwitchMockMvc.perform(post("/api/out-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outSwitch)))
            .andExpect(status().isCreated());

        // Validate the OutSwitch in the database
        List<OutSwitch> outSwitchList = outSwitchRepository.findAll();
        assertThat(outSwitchList).hasSize(databaseSizeBeforeCreate + 1);
        OutSwitch testOutSwitch = outSwitchList.get(outSwitchList.size() - 1);
        assertThat(testOutSwitch.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOutSwitch.getPinNumber()).isEqualTo(DEFAULT_PIN_NUMBER);
    }

    @Test
    @Transactional
    public void createOutSwitchWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = outSwitchRepository.findAll().size();

        // Create the OutSwitch with an existing ID
        OutSwitch existingOutSwitch = new OutSwitch();
        existingOutSwitch.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOutSwitchMockMvc.perform(post("/api/out-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingOutSwitch)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<OutSwitch> outSwitchList = outSwitchRepository.findAll();
        assertThat(outSwitchList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPinNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = outSwitchRepository.findAll().size();
        // set the field null
        outSwitch.setPinNumber(null);

        // Create the OutSwitch, which fails.

        restOutSwitchMockMvc.perform(post("/api/out-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outSwitch)))
            .andExpect(status().isBadRequest());

        List<OutSwitch> outSwitchList = outSwitchRepository.findAll();
        assertThat(outSwitchList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOutSwitches() throws Exception {
        // Initialize the database
        outSwitchRepository.saveAndFlush(outSwitch);

        // Get all the outSwitchList
        restOutSwitchMockMvc.perform(get("/api/out-switches?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(outSwitch.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].pinNumber").value(hasItem(DEFAULT_PIN_NUMBER)));
    }

    @Test
    @Transactional
    public void getOutSwitch() throws Exception {
        // Initialize the database
        outSwitchRepository.saveAndFlush(outSwitch);

        // Get the outSwitch
        restOutSwitchMockMvc.perform(get("/api/out-switches/{id}", outSwitch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(outSwitch.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.pinNumber").value(DEFAULT_PIN_NUMBER));
    }

    @Test
    @Transactional
    public void getNonExistingOutSwitch() throws Exception {
        // Get the outSwitch
        restOutSwitchMockMvc.perform(get("/api/out-switches/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOutSwitch() throws Exception {
        // Initialize the database
        outSwitchRepository.saveAndFlush(outSwitch);
        int databaseSizeBeforeUpdate = outSwitchRepository.findAll().size();

        // Update the outSwitch
        OutSwitch updatedOutSwitch = outSwitchRepository.findOne(outSwitch.getId());
        updatedOutSwitch
                .name(UPDATED_NAME)
                .pinNumber(UPDATED_PIN_NUMBER);

        restOutSwitchMockMvc.perform(put("/api/out-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOutSwitch)))
            .andExpect(status().isOk());

        // Validate the OutSwitch in the database
        List<OutSwitch> outSwitchList = outSwitchRepository.findAll();
        assertThat(outSwitchList).hasSize(databaseSizeBeforeUpdate);
        OutSwitch testOutSwitch = outSwitchList.get(outSwitchList.size() - 1);
        assertThat(testOutSwitch.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOutSwitch.getPinNumber()).isEqualTo(UPDATED_PIN_NUMBER);
    }

    @Test
    @Transactional
    public void updateNonExistingOutSwitch() throws Exception {
        int databaseSizeBeforeUpdate = outSwitchRepository.findAll().size();

        // Create the OutSwitch

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOutSwitchMockMvc.perform(put("/api/out-switches")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(outSwitch)))
            .andExpect(status().isCreated());

        // Validate the OutSwitch in the database
        List<OutSwitch> outSwitchList = outSwitchRepository.findAll();
        assertThat(outSwitchList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOutSwitch() throws Exception {
        // Initialize the database
        outSwitchRepository.saveAndFlush(outSwitch);
        int databaseSizeBeforeDelete = outSwitchRepository.findAll().size();

        // Get the outSwitch
        restOutSwitchMockMvc.perform(delete("/api/out-switches/{id}", outSwitch.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<OutSwitch> outSwitchList = outSwitchRepository.findAll();
        assertThat(outSwitchList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OutSwitch.class);
    }
}
