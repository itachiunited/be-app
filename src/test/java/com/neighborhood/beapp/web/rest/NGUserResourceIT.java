package com.neighborhood.beapp.web.rest;

import com.neighborhood.beapp.BeApp;
import com.neighborhood.beapp.domain.NGUser;
import com.neighborhood.beapp.repository.NGUserRepository;
import com.neighborhood.beapp.repository.search.NGUserSearchRepository;
import com.neighborhood.beapp.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;


import java.util.Collections;
import java.util.List;

import static com.neighborhood.beapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.neighborhood.beapp.domain.enumeration.VegetationType;
import com.neighborhood.beapp.domain.enumeration.Status;
/**
 * Integration tests for the {@Link NGUserResource} REST controller.
 */
@SpringBootTest(classes = BeApp.class)
public class NGUserResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final VegetationType DEFAULT_VEGETATION_TYPE = VegetationType.ORGANIC;
    private static final VegetationType UPDATED_VEGETATION_TYPE = VegetationType.NONORGANIC;

    private static final String DEFAULT_GARDEN_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_GARDEN_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Status DEFAULT_STATUS = Status.INVITED;
    private static final Status UPDATED_STATUS = Status.CONFIRMED;

    @Autowired
    private NGUserRepository nGUserRepository;

    /**
     * This repository is mocked in the com.neighborhood.beapp.repository.search test package.
     *
     * @see com.neighborhood.beapp.repository.search.NGUserSearchRepositoryMockConfiguration
     */
    @Autowired
    private NGUserSearchRepository mockNGUserSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restNGUserMockMvc;

    private NGUser nGUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final NGUserResource nGUserResource = new NGUserResource(nGUserRepository, mockNGUserSearchRepository);
        this.restNGUserMockMvc = MockMvcBuilders.standaloneSetup(nGUserResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NGUser createEntity() {
        NGUser nGUser = new NGUser()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .vegetationType(DEFAULT_VEGETATION_TYPE)
            .gardenDescription(DEFAULT_GARDEN_DESCRIPTION)
            .email(DEFAULT_EMAIL)
            .status(DEFAULT_STATUS);
        return nGUser;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NGUser createUpdatedEntity() {
        NGUser nGUser = new NGUser()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .vegetationType(UPDATED_VEGETATION_TYPE)
            .gardenDescription(UPDATED_GARDEN_DESCRIPTION)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS);
        return nGUser;
    }

    @BeforeEach
    public void initTest() {
        nGUserRepository.deleteAll();
        nGUser = createEntity();
    }

    @Test
    public void createNGUser() throws Exception {
        int databaseSizeBeforeCreate = nGUserRepository.findAll().size();

        // Create the NGUser
        restNGUserMockMvc.perform(post("/api/ng-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(nGUser)))
            .andExpect(status().isCreated());

        // Validate the NGUser in the database
        List<NGUser> nGUserList = nGUserRepository.findAll();
        assertThat(nGUserList).hasSize(databaseSizeBeforeCreate + 1);
        NGUser testNGUser = nGUserList.get(nGUserList.size() - 1);
        assertThat(testNGUser.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testNGUser.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testNGUser.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testNGUser.getVegetationType()).isEqualTo(DEFAULT_VEGETATION_TYPE);
        assertThat(testNGUser.getGardenDescription()).isEqualTo(DEFAULT_GARDEN_DESCRIPTION);
        assertThat(testNGUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testNGUser.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the NGUser in Elasticsearch
        verify(mockNGUserSearchRepository, times(1)).save(testNGUser);
    }

    @Test
    public void createNGUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = nGUserRepository.findAll().size();

        // Create the NGUser with an existing ID
        nGUser.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restNGUserMockMvc.perform(post("/api/ng-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(nGUser)))
            .andExpect(status().isBadRequest());

        // Validate the NGUser in the database
        List<NGUser> nGUserList = nGUserRepository.findAll();
        assertThat(nGUserList).hasSize(databaseSizeBeforeCreate);

        // Validate the NGUser in Elasticsearch
        verify(mockNGUserSearchRepository, times(0)).save(nGUser);
    }


    @Test
    public void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = nGUserRepository.findAll().size();
        // set the field null
        nGUser.setPhoneNumber(null);

        // Create the NGUser, which fails.

        restNGUserMockMvc.perform(post("/api/ng-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(nGUser)))
            .andExpect(status().isBadRequest());

        List<NGUser> nGUserList = nGUserRepository.findAll();
        assertThat(nGUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllNGUsers() throws Exception {
        // Initialize the database
        nGUserRepository.save(nGUser);

        // Get all the nGUserList
        restNGUserMockMvc.perform(get("/api/ng-users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nGUser.getId())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
            .andExpect(jsonPath("$.[*].vegetationType").value(hasItem(DEFAULT_VEGETATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].gardenDescription").value(hasItem(DEFAULT_GARDEN_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }
    
    @Test
    public void getNGUser() throws Exception {
        // Initialize the database
        nGUserRepository.save(nGUser);

        // Get the nGUser
        restNGUserMockMvc.perform(get("/api/ng-users/{id}", nGUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(nGUser.getId()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.toString()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.vegetationType").value(DEFAULT_VEGETATION_TYPE.toString()))
            .andExpect(jsonPath("$.gardenDescription").value(DEFAULT_GARDEN_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    public void getNonExistingNGUser() throws Exception {
        // Get the nGUser
        restNGUserMockMvc.perform(get("/api/ng-users/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateNGUser() throws Exception {
        // Initialize the database
        nGUserRepository.save(nGUser);

        int databaseSizeBeforeUpdate = nGUserRepository.findAll().size();

        // Update the nGUser
        NGUser updatedNGUser = nGUserRepository.findById(nGUser.getId()).get();
        updatedNGUser
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .vegetationType(UPDATED_VEGETATION_TYPE)
            .gardenDescription(UPDATED_GARDEN_DESCRIPTION)
            .email(UPDATED_EMAIL)
            .status(UPDATED_STATUS);

        restNGUserMockMvc.perform(put("/api/ng-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedNGUser)))
            .andExpect(status().isOk());

        // Validate the NGUser in the database
        List<NGUser> nGUserList = nGUserRepository.findAll();
        assertThat(nGUserList).hasSize(databaseSizeBeforeUpdate);
        NGUser testNGUser = nGUserList.get(nGUserList.size() - 1);
        assertThat(testNGUser.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testNGUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testNGUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testNGUser.getVegetationType()).isEqualTo(UPDATED_VEGETATION_TYPE);
        assertThat(testNGUser.getGardenDescription()).isEqualTo(UPDATED_GARDEN_DESCRIPTION);
        assertThat(testNGUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testNGUser.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the NGUser in Elasticsearch
        verify(mockNGUserSearchRepository, times(1)).save(testNGUser);
    }

    @Test
    public void updateNonExistingNGUser() throws Exception {
        int databaseSizeBeforeUpdate = nGUserRepository.findAll().size();

        // Create the NGUser

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNGUserMockMvc.perform(put("/api/ng-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(nGUser)))
            .andExpect(status().isBadRequest());

        // Validate the NGUser in the database
        List<NGUser> nGUserList = nGUserRepository.findAll();
        assertThat(nGUserList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NGUser in Elasticsearch
        verify(mockNGUserSearchRepository, times(0)).save(nGUser);
    }

    @Test
    public void deleteNGUser() throws Exception {
        // Initialize the database
        nGUserRepository.save(nGUser);

        int databaseSizeBeforeDelete = nGUserRepository.findAll().size();

        // Delete the nGUser
        restNGUserMockMvc.perform(delete("/api/ng-users/{id}", nGUser.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<NGUser> nGUserList = nGUserRepository.findAll();
        assertThat(nGUserList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the NGUser in Elasticsearch
        verify(mockNGUserSearchRepository, times(1)).deleteById(nGUser.getId());
    }

    @Test
    public void searchNGUser() throws Exception {
        // Initialize the database
        nGUserRepository.save(nGUser);
        when(mockNGUserSearchRepository.search(queryStringQuery("id:" + nGUser.getId())))
            .thenReturn(Collections.singletonList(nGUser));
        // Search the nGUser
        restNGUserMockMvc.perform(get("/api/_search/ng-users?query=id:" + nGUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(nGUser.getId())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].vegetationType").value(hasItem(DEFAULT_VEGETATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].gardenDescription").value(hasItem(DEFAULT_GARDEN_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NGUser.class);
        NGUser nGUser1 = new NGUser();
        nGUser1.setId("id1");
        NGUser nGUser2 = new NGUser();
        nGUser2.setId(nGUser1.getId());
        assertThat(nGUser1).isEqualTo(nGUser2);
        nGUser2.setId("id2");
        assertThat(nGUser1).isNotEqualTo(nGUser2);
        nGUser1.setId(null);
        assertThat(nGUser1).isNotEqualTo(nGUser2);
    }
}
