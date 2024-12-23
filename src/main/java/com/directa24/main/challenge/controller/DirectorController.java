package com.directa24.main.challenge.controller;

import com.directa24.main.challenge.service.DirectorService;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/directors")
public class DirectorController {

    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    /**
     * Retrieves a list of directors who have directed more than the specified number of movies.
     *
     * <p>This endpoint fetches directors whose movie count exceeds the given threshold.
     * The response is returned as a JSON object containing the list of directors.</p>
     *
     * @param threshold the minimum number of movies a director must have directed (must be 0 or greater).
     * @return a {@link ResponseEntity} containing a JSON object with the list of directors.
     */
    @GetMapping
    public ResponseEntity<Map<String, List<String>>> getDirectors(
            @RequestParam("threshold")
            @Min(value = 0, message = "Threshold must be 0 or greater") int threshold) {

        List<String> directors = directorService.getDirectors(threshold);
        return ResponseEntity.ok(Map.of("directors", directors));
    }

}
