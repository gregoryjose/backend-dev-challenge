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
     * Fetch directors who meet the threshold of movie counts.
     *
     * @param threshold The minimum number of movies a director must have directed.
     * @return A JSON response with the list of directors.
     */
    @GetMapping
    public ResponseEntity<Map<String, List<String>>> getDirectors(
            @RequestParam("threshold")
            @Min(value = 0, message = "Threshold must be 0 or greater") int threshold) {

        List<String> directors = directorService.getDirectors(threshold);
        return ResponseEntity.ok(Map.of("directors", directors));
    }

}
