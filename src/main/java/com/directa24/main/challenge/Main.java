package com.directa24.main.challenge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Main {

   public static final String BASE_URL = "https://wiremock.dev.eroninternational.com/api/movies/search?page=";
   public static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
   public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   public static void main(String[] args) {
      try {
         List<String> directors = getDirectors(3);
         System.out.println(String.join("\n", directors));
      } catch (Exception e) {
         System.err.println("Error fetching directors: " + e.getMessage());
      }
   }

   /*
    * Complete the 'getDirectors' function below.
    *
    * The function is expected to return a List<String>.
    * The function accepts int threshold as parameter.
    *
    * URL
    * https://wiremock.dev.eroninternational.com/api/movies/search?page=<pageNumber>
    */
   public static List<String> getDirectors(int threshold) {

      Map<String, Integer> directorMovieCount = new HashMap<>();
      int page = 1;
      boolean hasMorePages;

      do {
         JsonNode pageData = fetchPageData(page);
         if (pageData == null) break;

         // Update director movie count
         updateDirectorMovieCount(pageData, directorMovieCount);

         int totalPages = pageData.path("total_pages").asInt(1);
         hasMorePages = page < totalPages;
         page++;
      } while (hasMorePages);

      // Filter directors based on the threshold
      return directorMovieCount.entrySet().stream()
              .filter(entry -> entry.getValue() > threshold)
              .map(Map.Entry::getKey)
              .sorted()
              .toList();
   }

   /**
    * Fetch the JSON data for a specific page.
    */
   private static JsonNode fetchPageData(int pageNumber) {
      try {
         URI uri = URI.create(BASE_URL + pageNumber);
         HttpRequest request = HttpRequest.newBuilder()
                 .uri(uri)
                 .GET()
                 .build();

         HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
         if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP request failed with status: " + response.statusCode());
         }

         return OBJECT_MAPPER.readTree(response.body());
      } catch (Exception e) {
         throw new RuntimeException("Failed to fetch or parse page data for page: " + pageNumber, e);
      }
   }

   /**
    * Update the director movie count based on data from the current page.
    */
   private static void updateDirectorMovieCount(JsonNode pageData, Map<String, Integer> directorMovieCount) {
      JsonNode movies = pageData.path("data");

      if (!movies.isArray()) {
         throw new RuntimeException("Invalid JSON structure: 'data' is not an array.");
      }

      for (JsonNode movie : movies) {
         String director = movie.path("Director").asText();
         directorMovieCount.put(director, directorMovieCount.getOrDefault(director, 0) + 1);
      }
   }



}
