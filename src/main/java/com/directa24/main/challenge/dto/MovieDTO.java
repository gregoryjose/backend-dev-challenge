package com.directa24.main.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MovieDTO {

    @JsonProperty("Director")
    private String director;


    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }
}
