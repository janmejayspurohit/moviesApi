package com.movies.moviesApi.service;

import com.movies.moviesApi.dto.MovieDto;
import com.movies.moviesApi.dto.PaginatedMovie;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {

    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(Integer movieId);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException;

    String deleteMovie(Integer movieId) throws IOException;

    PaginatedMovie getAllMoviesPaginated(Integer pageNumber, Integer pageSize);

    PaginatedMovie getAllMoviesPaginatedWithSort(Integer pageNumber, Integer pageSize, String sortBy, String dir);
}
