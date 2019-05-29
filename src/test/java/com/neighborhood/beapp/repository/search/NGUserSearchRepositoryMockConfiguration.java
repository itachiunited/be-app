package com.neighborhood.beapp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link NGUserSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class NGUserSearchRepositoryMockConfiguration {

    @MockBean
    private NGUserSearchRepository mockNGUserSearchRepository;

}
