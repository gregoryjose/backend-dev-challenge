package com.directa24.main.challenge.service.impl;

import com.directa24.main.challenge.api.MovieApiClient;
import com.directa24.main.challenge.dto.MovieDTO;
import com.directa24.main.challenge.dto.MoviePageDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DirectorServiceImplTest {

    @Mock
    private MovieApiClient movieApiClient;

    @InjectMocks
    private DirectorServiceImpl directorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getDirectors_positiveScenario() {
        // Mock data
        MovieDTO movie1 = new MovieDTO("Director A");
        MovieDTO movie2 = new MovieDTO("Director B");
        MovieDTO movie3 = new MovieDTO("Director A");

        MoviePageDTO page1 = new MoviePageDTO(Arrays.asList(movie1, movie2, movie3), 2);
        MoviePageDTO page2 = new MoviePageDTO(Collections.singletonList(new MovieDTO("Director C")), 2);

        // Mock behavior
        when(movieApiClient.fetchMovies(1)).thenReturn(page1);
        when(movieApiClient.fetchMovies(2)).thenReturn(page2);

        // Execute
        List<String> result = directorService.getDirectors(1);

        // Verify
        // Director A meets the threshold (2 movies > 1).
        assertEquals(Collections.singletonList("Director A"), result);
        verify(movieApiClient, times(2)).fetchMovies(anyInt());
    }

    @Test
    void getDirectors_thresholdExceedsAllDirectors() {
        // Mock data
        MovieDTO movie1 = new MovieDTO("Director A");
        MoviePageDTO page1 = new MoviePageDTO(Collections.singletonList(movie1), 1);

        // Mock behavior
        when(movieApiClient.fetchMovies(1)).thenReturn(page1);

        // Execute
        List<String> result = directorService.getDirectors(2);

        // Verify
        assertEquals(Collections.emptyList(), result);
        verify(movieApiClient, times(1)).fetchMovies(1);
    }

    @Test
    void getDirectors_emptyResponseFromApi() {
        // Mock behavior
        when(movieApiClient.fetchMovies(1)).thenReturn(new MoviePageDTO(Collections.emptyList(), 1));

        // Execute
        List<String> result = directorService.getDirectors(1);

        // Verify
        assertEquals(Collections.emptyList(), result);
        verify(movieApiClient, times(1)).fetchMovies(1);
    }

    @Test
    void getDirectors_apiThrowsException() {
        // Mock behavior
        when(movieApiClient.fetchMovies(1)).thenThrow(new RuntimeException("API error"));

        // Execute & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> directorService.getDirectors(1));
        assertEquals("API error", exception.getMessage());

        verify(movieApiClient, times(1)).fetchMovies(1);
    }

}