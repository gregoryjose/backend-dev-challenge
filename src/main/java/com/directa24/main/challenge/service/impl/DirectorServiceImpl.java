package com.directa24.main.challenge.service.impl;

import com.directa24.main.challenge.api.MovieApiClient;
import com.directa24.main.challenge.dto.MovieDTO;
import com.directa24.main.challenge.dto.MoviePageDTO;
import com.directa24.main.challenge.service.DirectorService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DirectorServiceImpl implements DirectorService {

    private final MovieApiClient movieApiClient;

    public DirectorServiceImpl(MovieApiClient movieApiClient) {
        this.movieApiClient = movieApiClient;
    }

    @Override
    public List<String> getDirectors(int threshold) {
        Map<String, Integer> directorMovieCount = new HashMap<>();
        int page = 1;
        boolean hasMorePages;

        do {
            MoviePageDTO pageData = fetchMoviesWithRetry(page);
            if (pageData == null || pageData.getData() == null) break;

            updateDirectorMovieCount(pageData.getData(), directorMovieCount);

            int totalPages = pageData.getTotalPages();
            hasMorePages = page < totalPages;
            page++;
        } while (hasMorePages);

        return directorMovieCount.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Fetch movies with retry mechanism.
     */
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2) // 1-second delay with exponential backoff
    )
    private MoviePageDTO fetchMoviesWithRetry(int page) {
        return movieApiClient.fetchMovies(page);
    }

    private void updateDirectorMovieCount(List<MovieDTO> movies, Map<String, Integer> directorMovieCount) {
        for (MovieDTO movie : movies) {
            String director = movie.getDirector();
            directorMovieCount.put(director, directorMovieCount.getOrDefault(director, 0) + 1);
        }
    }
}
