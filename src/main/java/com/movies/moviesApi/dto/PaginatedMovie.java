package com.movies.moviesApi.dto;

import java.util.List;

public record PaginatedMovie(List<MovieDto> movieDtos, Integer pageNumber, Integer pageSize, long totalElements,
                             int totalPages, boolean isLast) {
}
