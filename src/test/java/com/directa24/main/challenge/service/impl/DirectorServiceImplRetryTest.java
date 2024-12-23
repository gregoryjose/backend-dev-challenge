package com.directa24.main.challenge.service.impl;

import com.directa24.main.challenge.api.MovieApiClient;
import com.directa24.main.challenge.dto.MovieDTO;
import com.directa24.main.challenge.dto.MoviePageDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DirectorServiceImplRetryTest {

    @MockitoBean
    private MovieApiClient movieApiClient;

    @Autowired
    private DirectorServiceImpl directorService;

    @Test
    void getDirectors_retryMechanism_successAfterRetry() {
        // Mock data
        MovieDTO movie1 = new MovieDTO("Director A"); // Appears twice
        MovieDTO movie2 = new MovieDTO("Director B"); // Appears once
        MovieDTO movie3 = new MovieDTO("Director A");

        MoviePageDTO page1 = new MoviePageDTO(Arrays.asList(movie1, movie2, movie3), 1);

        // Mock behavior: First attempt fails, second attempt succeeds
        when(movieApiClient.fetchMovies(1))
                .thenThrow(new RuntimeException("Temporary error"))
                .thenReturn(page1);

        // Execute
        List<String> result = directorService.getDirectors(1);

        // Verify: Director A meets the threshold (2 movies > threshold 1)
        assertEquals(Collections.singletonList("Director A"), result);
        verify(movieApiClient, times(2)).fetchMovies(1); // Ensure two calls (retry happened)
    }

    @Test
    void getDirectors_retryMechanism_failAfterMaxAttempts() {
        // Mock behavior: All attempts fail
        when(movieApiClient.fetchMovies(1)).thenThrow(new RuntimeException("Permanent error"));

        // Execute & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> directorService.getDirectors(1));
        assertEquals("Permanent error", exception.getMessage());

        // Verify that the retry mechanism invoked the method 3 times
        verify(movieApiClient, times(3)).fetchMovies(1); // Max retries reached
    }

    @Test
    void getDirectors_noRetryNeeded() {
        // Mock data
        MovieDTO movie1 = new MovieDTO("Director A");
        MovieDTO movie2 = new MovieDTO("Director A");
        MovieDTO movie3 = new MovieDTO("Director B");

        MoviePageDTO page1 = new MoviePageDTO(Arrays.asList(movie1, movie2, movie3), 1);

        // Mock behavior: Success on the first call
        when(movieApiClient.fetchMovies(1)).thenReturn(page1);

        // Execute
        List<String> result = directorService.getDirectors(1);

        // Verify: Director A meets the threshold (2 movies > threshold 1)
        assertEquals(Collections.singletonList("Director A"), result);
        verify(movieApiClient, times(1)).fetchMovies(1); // Ensure only one call
    }
}
