package com.movies.moviesApi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    private Integer movieId;

    @NotBlank(message = "Movie title cannot be empty")
    private String title;

    @NotBlank(message = "Movie director cannot be empty")
    private String director;

    @NotBlank(message = "Movie studio cannot be empty")
    private String studio;

    private Set<String> movieCast;

    private Integer releaseYear;

    @NotBlank(message = "Movie poster cannot be empty")
    private String poster;

    @NotBlank(message = "Movie poster url cannot be empty")
    private String posterUrl;
}
