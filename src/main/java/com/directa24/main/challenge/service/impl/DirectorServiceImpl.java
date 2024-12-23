package com.directa24.main.challenge.service.impl;

import com.directa24.main.challenge.api.MovieApiClient;
import com.directa24.main.challenge.dto.MovieDTO;
import com.directa24.main.challenge.dto.MoviePageDTO;
import com.directa24.main.challenge.service.DirectorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DirectorServiceImpl implements DirectorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectorServiceImpl.class);
    private final MovieApiClient movieApiClient;

    public DirectorServiceImpl(MovieApiClient movieApiClient) {
        this.movieApiClient = movieApiClient;
    }

    /**
     * Retrieves directors with movie counts above the specified threshold.
     *
     * <p>Processes paginated API data to compute movie counts for directors
     * and returns a sorted list of directors whose movie counts exceed the
     * threshold. Retries the operation up to three times for API failures.</p>
     *
     * @param threshold minimum number of movies a director must have directed
     * @return a sorted {@link List} of director names, or an empty list if none qualify
     * @throws RuntimeException if all retry attempts to fetch API data fail
     */
    @Override
    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public List<String> getDirectors(int threshold) {
        LOGGER.info("Fetching directors with a threshold of {} movies.", threshold);

        Map<String, Integer> directorMovieCount = new HashMap<>();
        int page = 1;
        boolean hasMorePages;

        do {
            LOGGER.debug("Fetching page {} from the downstream API.", page);

            MoviePageDTO pageData = movieApiClient.fetchMovies(page);

            if (pageData == null || pageData.getData() == null) {
                LOGGER.warn("No data found on page {}.", page);
                break;
            }

            updateDirectorMovieCount(pageData.getData(), directorMovieCount);

            int totalPages = pageData.getTotalPages();
            hasMorePages = page < totalPages;
            page++;
        } while (hasMorePages);

        LOGGER.debug("Fetched {} directors with {} movies.", directorMovieCount, threshold);

        List<String> directors = directorMovieCount.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());

        LOGGER.info("Successfully fetched {} directors exceeding the threshold.", directors.size());
        return directors;
    }

    /**
     * Updates movie counts for directors based on the provided movie list.
     *
     * @param movies the list of {@link MovieDTO} objects
     * @param directorMovieCount the map tracking movie counts per director
     */
    private void updateDirectorMovieCount(List<MovieDTO> movies, Map<String, Integer> directorMovieCount) {
        for (MovieDTO movie : movies) {
            String director = movie.getDirector();
            directorMovieCount.put(director, directorMovieCount.getOrDefault(director, 0) + 1);
        }
        LOGGER.debug("Updated director movie counts. Current size: {}", directorMovieCount.size());
    }
}
