package com.directa24.main.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MoviePageDTO {

    @JsonProperty("total_pages")
    private int totalPages;
    private List<MovieDTO> data;

    public MoviePageDTO(List<MovieDTO> data, int totalPages) {
        this.totalPages = totalPages;
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<MovieDTO> getData() {
        return data;
    }

    public void setData(List<MovieDTO> data) {
        this.data = data;
    }
}
