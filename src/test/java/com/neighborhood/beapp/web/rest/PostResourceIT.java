package com.neighborhood.beapp.web.rest;

import com.neighborhood.beapp.BeApp;
import com.neighborhood.beapp.domain.Post;
import com.neighborhood.beapp.repository.PostRepository;
import com.neighborhood.beapp.repository.search.PostSearchRepository;
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


import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static com.neighborhood.beapp.web.rest.TestUtil.sameInstant;
import static com.neighborhood.beapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@Link PostResource} REST controller.
 */
@SpringBootTest(classes = BeApp.class)
public class PostResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;

    private static final Float DEFAULT_MSRP = 1F;
    private static final Float UPDATED_MSRP = 2F;

    private static final Float DEFAULT_WEIGHT = 1F;
    private static final Float UPDATED_WEIGHT = 2F;

    private static final Integer DEFAULT_AVAILABLE_QTY = 1;
    private static final Integer UPDATED_AVAILABLE_QTY = 2;

    private static final Integer DEFAULT_REMAINING_QTY = 1;
    private static final Integer UPDATED_REMAINING_QTY = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_MORE_INFO = "AAAAAAAAAA";
    private static final String UPDATED_MORE_INFO = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_END_TIME = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_END_TIME = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private PostRepository postRepository;

    /**
     * This repository is mocked in the com.neighborhood.beapp.repository.search test package.
     *
     * @see com.neighborhood.beapp.repository.search.PostSearchRepositoryMockConfiguration
     */
    @Autowired
    private PostSearchRepository mockPostSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restPostMockMvc;

    private Post post;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PostResource postResource = new PostResource(postRepository, mockPostSearchRepository);
        this.restPostMockMvc = MockMvcBuilders.standaloneSetup(postResource)
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
    public static Post createEntity() {
        Post post = new Post()
            .title(DEFAULT_TITLE)
            .price(DEFAULT_PRICE)
            .msrp(DEFAULT_MSRP)
            .weight(DEFAULT_WEIGHT)
            .availableQty(DEFAULT_AVAILABLE_QTY)
            .remainingQty(DEFAULT_REMAINING_QTY)
            .description(DEFAULT_DESCRIPTION)
            .moreInfo(DEFAULT_MORE_INFO)
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME);
        return post;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Post createUpdatedEntity() {
        Post post = new Post()
            .title(UPDATED_TITLE)
            .price(UPDATED_PRICE)
            .msrp(UPDATED_MSRP)
            .weight(UPDATED_WEIGHT)
            .availableQty(UPDATED_AVAILABLE_QTY)
            .remainingQty(UPDATED_REMAINING_QTY)
            .description(UPDATED_DESCRIPTION)
            .moreInfo(UPDATED_MORE_INFO)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);
        return post;
    }

    @BeforeEach
    public void initTest() {
        postRepository.deleteAll();
        post = createEntity();
    }

    @Test
    public void createPost() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isCreated());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate + 1);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPost.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testPost.getMsrp()).isEqualTo(DEFAULT_MSRP);
        assertThat(testPost.getWeight()).isEqualTo(DEFAULT_WEIGHT);
        assertThat(testPost.getAvailableQty()).isEqualTo(DEFAULT_AVAILABLE_QTY);
        assertThat(testPost.getRemainingQty()).isEqualTo(DEFAULT_REMAINING_QTY);
        assertThat(testPost.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPost.getMoreInfo()).isEqualTo(DEFAULT_MORE_INFO);
        assertThat(testPost.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testPost.getEndTime()).isEqualTo(DEFAULT_END_TIME);

        // Validate the Post in Elasticsearch
        verify(mockPostSearchRepository, times(1)).save(testPost);
    }

    @Test
    public void createPostWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = postRepository.findAll().size();

        // Create the Post with an existing ID
        post.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restPostMockMvc.perform(post("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeCreate);

        // Validate the Post in Elasticsearch
        verify(mockPostSearchRepository, times(0)).save(post);
    }


    @Test
    public void getAllPosts() throws Exception {
        // Initialize the database
        postRepository.save(post);

        // Get all the postList
        restPostMockMvc.perform(get("/api/posts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].msrp").value(hasItem(DEFAULT_MSRP.doubleValue())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT.doubleValue())))
            .andExpect(jsonPath("$.[*].availableQty").value(hasItem(DEFAULT_AVAILABLE_QTY)))
            .andExpect(jsonPath("$.[*].remainingQty").value(hasItem(DEFAULT_REMAINING_QTY)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].moreInfo").value(hasItem(DEFAULT_MORE_INFO.toString())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(sameInstant(DEFAULT_START_TIME))))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(sameInstant(DEFAULT_END_TIME))));
    }
    
    @Test
    public void getPost() throws Exception {
        // Initialize the database
        postRepository.save(post);

        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(post.getId()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.msrp").value(DEFAULT_MSRP.doubleValue()))
            .andExpect(jsonPath("$.weight").value(DEFAULT_WEIGHT.doubleValue()))
            .andExpect(jsonPath("$.availableQty").value(DEFAULT_AVAILABLE_QTY))
            .andExpect(jsonPath("$.remainingQty").value(DEFAULT_REMAINING_QTY))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.moreInfo").value(DEFAULT_MORE_INFO.toString()))
            .andExpect(jsonPath("$.startTime").value(sameInstant(DEFAULT_START_TIME)))
            .andExpect(jsonPath("$.endTime").value(sameInstant(DEFAULT_END_TIME)));
    }

    @Test
    public void getNonExistingPost() throws Exception {
        // Get the post
        restPostMockMvc.perform(get("/api/posts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updatePost() throws Exception {
        // Initialize the database
        postRepository.save(post);

        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Update the post
        Post updatedPost = postRepository.findById(post.getId()).get();
        updatedPost
            .title(UPDATED_TITLE)
            .price(UPDATED_PRICE)
            .msrp(UPDATED_MSRP)
            .weight(UPDATED_WEIGHT)
            .availableQty(UPDATED_AVAILABLE_QTY)
            .remainingQty(UPDATED_REMAINING_QTY)
            .description(UPDATED_DESCRIPTION)
            .moreInfo(UPDATED_MORE_INFO)
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME);

        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPost)))
            .andExpect(status().isOk());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);
        Post testPost = postList.get(postList.size() - 1);
        assertThat(testPost.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPost.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testPost.getMsrp()).isEqualTo(UPDATED_MSRP);
        assertThat(testPost.getWeight()).isEqualTo(UPDATED_WEIGHT);
        assertThat(testPost.getAvailableQty()).isEqualTo(UPDATED_AVAILABLE_QTY);
        assertThat(testPost.getRemainingQty()).isEqualTo(UPDATED_REMAINING_QTY);
        assertThat(testPost.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPost.getMoreInfo()).isEqualTo(UPDATED_MORE_INFO);
        assertThat(testPost.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testPost.getEndTime()).isEqualTo(UPDATED_END_TIME);

        // Validate the Post in Elasticsearch
        verify(mockPostSearchRepository, times(1)).save(testPost);
    }

    @Test
    public void updateNonExistingPost() throws Exception {
        int databaseSizeBeforeUpdate = postRepository.findAll().size();

        // Create the Post

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPostMockMvc.perform(put("/api/posts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(post)))
            .andExpect(status().isBadRequest());

        // Validate the Post in the database
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Post in Elasticsearch
        verify(mockPostSearchRepository, times(0)).save(post);
    }

    @Test
    public void deletePost() throws Exception {
        // Initialize the database
        postRepository.save(post);

        int databaseSizeBeforeDelete = postRepository.findAll().size();

        // Delete the post
        restPostMockMvc.perform(delete("/api/posts/{id}", post.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Post> postList = postRepository.findAll();
        assertThat(postList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Post in Elasticsearch
        verify(mockPostSearchRepository, times(1)).deleteById(post.getId());
    }

    @Test
    public void searchPost() throws Exception {
        // Initialize the database
        postRepository.save(post);
        when(mockPostSearchRepository.search(queryStringQuery("id:" + post.getId())))
            .thenReturn(Collections.singletonList(post));
        // Search the post
        restPostMockMvc.perform(get("/api/_search/posts?query=id:" + post.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(post.getId())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].msrp").value(hasItem(DEFAULT_MSRP.doubleValue())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT.doubleValue())))
            .andExpect(jsonPath("$.[*].availableQty").value(hasItem(DEFAULT_AVAILABLE_QTY)))
            .andExpect(jsonPath("$.[*].remainingQty").value(hasItem(DEFAULT_REMAINING_QTY)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].moreInfo").value(hasItem(DEFAULT_MORE_INFO)))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(sameInstant(DEFAULT_START_TIME))))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(sameInstant(DEFAULT_END_TIME))));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Post.class);
        Post post1 = new Post();
        post1.setId("id1");
        Post post2 = new Post();
        post2.setId(post1.getId());
        assertThat(post1).isEqualTo(post2);
        post2.setId("id2");
        assertThat(post1).isNotEqualTo(post2);
        post1.setId(null);
        assertThat(post1).isNotEqualTo(post2);
    }
}
