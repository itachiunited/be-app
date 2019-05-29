package com.neighborhood.beapp.web.rest;

import com.neighborhood.beapp.BeApp;
import com.neighborhood.beapp.domain.Images;
import com.neighborhood.beapp.repository.ImagesRepository;
import com.neighborhood.beapp.repository.search.ImagesSearchRepository;
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

import com.neighborhood.beapp.domain.enumeration.ImageType;
/**
 * Integration tests for the {@Link ImagesResource} REST controller.
 */
@SpringBootTest(classes = BeApp.class)
public class ImagesResourceIT {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final ImageType DEFAULT_IMAGE_TYPE = ImageType.POST;
    private static final ImageType UPDATED_IMAGE_TYPE = ImageType.PRODUCT;

    @Autowired
    private ImagesRepository imagesRepository;

    /**
     * This repository is mocked in the com.neighborhood.beapp.repository.search test package.
     *
     * @see com.neighborhood.beapp.repository.search.ImagesSearchRepositoryMockConfiguration
     */
    @Autowired
    private ImagesSearchRepository mockImagesSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restImagesMockMvc;

    private Images images;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ImagesResource imagesResource = new ImagesResource(imagesRepository, mockImagesSearchRepository);
        this.restImagesMockMvc = MockMvcBuilders.standaloneSetup(imagesResource)
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
    public static Images createEntity() {
        Images images = new Images()
            .imageURL(DEFAULT_IMAGE_URL)
            .imageType(DEFAULT_IMAGE_TYPE);
        return images;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Images createUpdatedEntity() {
        Images images = new Images()
            .imageURL(UPDATED_IMAGE_URL)
            .imageType(UPDATED_IMAGE_TYPE);
        return images;
    }

    @BeforeEach
    public void initTest() {
        imagesRepository.deleteAll();
        images = createEntity();
    }

    @Test
    public void createImages() throws Exception {
        int databaseSizeBeforeCreate = imagesRepository.findAll().size();

        // Create the Images
        restImagesMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(images)))
            .andExpect(status().isCreated());

        // Validate the Images in the database
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeCreate + 1);
        Images testImages = imagesList.get(imagesList.size() - 1);
        assertThat(testImages.getImageURL()).isEqualTo(DEFAULT_IMAGE_URL);
        assertThat(testImages.getImageType()).isEqualTo(DEFAULT_IMAGE_TYPE);

        // Validate the Images in Elasticsearch
        verify(mockImagesSearchRepository, times(1)).save(testImages);
    }

    @Test
    public void createImagesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imagesRepository.findAll().size();

        // Create the Images with an existing ID
        images.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restImagesMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(images)))
            .andExpect(status().isBadRequest());

        // Validate the Images in the database
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeCreate);

        // Validate the Images in Elasticsearch
        verify(mockImagesSearchRepository, times(0)).save(images);
    }


    @Test
    public void checkImageURLIsRequired() throws Exception {
        int databaseSizeBeforeTest = imagesRepository.findAll().size();
        // set the field null
        images.setImageURL(null);

        // Create the Images, which fails.

        restImagesMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(images)))
            .andExpect(status().isBadRequest());

        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkImageTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = imagesRepository.findAll().size();
        // set the field null
        images.setImageType(null);

        // Create the Images, which fails.

        restImagesMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(images)))
            .andExpect(status().isBadRequest());

        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllImages() throws Exception {
        // Initialize the database
        imagesRepository.save(images);

        // Get all the imagesList
        restImagesMockMvc.perform(get("/api/images?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(images.getId())))
            .andExpect(jsonPath("$.[*].imageURL").value(hasItem(DEFAULT_IMAGE_URL.toString())))
            .andExpect(jsonPath("$.[*].imageType").value(hasItem(DEFAULT_IMAGE_TYPE.toString())));
    }
    
    @Test
    public void getImages() throws Exception {
        // Initialize the database
        imagesRepository.save(images);

        // Get the images
        restImagesMockMvc.perform(get("/api/images/{id}", images.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(images.getId()))
            .andExpect(jsonPath("$.imageURL").value(DEFAULT_IMAGE_URL.toString()))
            .andExpect(jsonPath("$.imageType").value(DEFAULT_IMAGE_TYPE.toString()));
    }

    @Test
    public void getNonExistingImages() throws Exception {
        // Get the images
        restImagesMockMvc.perform(get("/api/images/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateImages() throws Exception {
        // Initialize the database
        imagesRepository.save(images);

        int databaseSizeBeforeUpdate = imagesRepository.findAll().size();

        // Update the images
        Images updatedImages = imagesRepository.findById(images.getId()).get();
        updatedImages
            .imageURL(UPDATED_IMAGE_URL)
            .imageType(UPDATED_IMAGE_TYPE);

        restImagesMockMvc.perform(put("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedImages)))
            .andExpect(status().isOk());

        // Validate the Images in the database
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeUpdate);
        Images testImages = imagesList.get(imagesList.size() - 1);
        assertThat(testImages.getImageURL()).isEqualTo(UPDATED_IMAGE_URL);
        assertThat(testImages.getImageType()).isEqualTo(UPDATED_IMAGE_TYPE);

        // Validate the Images in Elasticsearch
        verify(mockImagesSearchRepository, times(1)).save(testImages);
    }

    @Test
    public void updateNonExistingImages() throws Exception {
        int databaseSizeBeforeUpdate = imagesRepository.findAll().size();

        // Create the Images

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restImagesMockMvc.perform(put("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(images)))
            .andExpect(status().isBadRequest());

        // Validate the Images in the database
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Images in Elasticsearch
        verify(mockImagesSearchRepository, times(0)).save(images);
    }

    @Test
    public void deleteImages() throws Exception {
        // Initialize the database
        imagesRepository.save(images);

        int databaseSizeBeforeDelete = imagesRepository.findAll().size();

        // Delete the images
        restImagesMockMvc.perform(delete("/api/images/{id}", images.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Images> imagesList = imagesRepository.findAll();
        assertThat(imagesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Images in Elasticsearch
        verify(mockImagesSearchRepository, times(1)).deleteById(images.getId());
    }

    @Test
    public void searchImages() throws Exception {
        // Initialize the database
        imagesRepository.save(images);
        when(mockImagesSearchRepository.search(queryStringQuery("id:" + images.getId())))
            .thenReturn(Collections.singletonList(images));
        // Search the images
        restImagesMockMvc.perform(get("/api/_search/images?query=id:" + images.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(images.getId())))
            .andExpect(jsonPath("$.[*].imageURL").value(hasItem(DEFAULT_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].imageType").value(hasItem(DEFAULT_IMAGE_TYPE.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Images.class);
        Images images1 = new Images();
        images1.setId("id1");
        Images images2 = new Images();
        images2.setId(images1.getId());
        assertThat(images1).isEqualTo(images2);
        images2.setId("id2");
        assertThat(images1).isNotEqualTo(images2);
        images1.setId(null);
        assertThat(images1).isNotEqualTo(images2);
    }
}
