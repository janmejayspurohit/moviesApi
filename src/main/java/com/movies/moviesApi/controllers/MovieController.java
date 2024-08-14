package com.movies.moviesApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movies.moviesApi.dto.MovieDto;
import com.movies.moviesApi.dto.PaginatedMovie;
import com.movies.moviesApi.service.MovieService;
import com.movies.moviesApi.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file,
                                                    @RequestPart String movieDto) throws IOException {
        MovieDto dto = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId) {
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMoviesHandler() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId,
                                                       @RequestPart MultipartFile file,
                                                       @RequestPart String movieDtoObj) throws IOException {
        if (file.isEmpty()) file = null;
        return ResponseEntity.ok(movieService.updateMovie(movieId, convertToMovieDto(movieDtoObj), file));
    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    @GetMapping("/allMoviesPaginated")
    public ResponseEntity<PaginatedMovie> getMoviesPaginatedHandler(@RequestParam(defaultValue =
            AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                    @RequestParam(defaultValue =
                                                                            AppConstants.PAGE_SIZE,
                                                                            required = false) Integer pageSize) {
        return ResponseEntity.ok(movieService.getAllMoviesPaginated(pageNumber, pageSize));
    }

    @GetMapping("/allMoviesPaginatedAndSorted")
    public ResponseEntity<PaginatedMovie> getMoviesPaginatedAndSortedHandler(@RequestParam(defaultValue =
            AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                             @RequestParam(defaultValue =
                                                                                     AppConstants.PAGE_SIZE
                                                                                     , required = false) Integer pageSize,
                                                                             @RequestParam(defaultValue =
                                                                                     AppConstants.SORT_BY,
                                                                                     required = false) String sortBy,
                                                                             @RequestParam(defaultValue =
                                                                                     AppConstants.SORT_DIR,
                                                                                     required = false) String dir) {
        return ResponseEntity.ok(movieService.getAllMoviesPaginatedWithSort(pageNumber,
                                                                            pageSize,
                                                                            sortBy,
                                                                            dir));
    }

    private MovieDto convertToMovieDto(String movieDtoObj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(movieDtoObj, MovieDto.class);
    }
}
