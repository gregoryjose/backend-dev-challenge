package com.directa24.main.challenge.api;

import com.directa24.main.challenge.dto.MoviePageDTO;
import com.directa24.main.challenge.util.ApiConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MovieApiClient {

    private final WebClient webClient;

    public MovieApiClient(@Value("${api.base-url}") String baseUrl,
                          WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Fetch movies for a specific page and map it to a DTO.
     * @param pageNumber the page to fetch
     * @return MoviePageDTO containing page data
     */
    public MoviePageDTO fetchMovies(int pageNumber) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(ApiConstants.MOVIES_RESOURCE + "/search")
                        .queryParam(ApiConstants.QUERY_PARAM_PAGE, pageNumber)
                        .build())
                .retrieve()
                .bodyToMono(MoviePageDTO.class)
                .block();
    }
}
