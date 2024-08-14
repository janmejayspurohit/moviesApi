package com.movies.moviesApi.service;

import com.movies.moviesApi.dto.MovieDto;
import com.movies.moviesApi.dto.PaginatedMovie;
import com.movies.moviesApi.entities.Movie;
import com.movies.moviesApi.exceptions.MovieNotFoundException;
import com.movies.moviesApi.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.posters}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        String uploadedFileName = fileService.uploadFile(path, file);
        movieDto.setPoster(uploadedFileName);
        Movie movie = new Movie(null,
                                movieDto.getTitle(),
                                movieDto.getDirector(),
                                movieDto.getStudio(),
                                movieDto.getMovieCast(),
                                movieDto.getReleaseYear(),
                                movieDto.getPoster());
        Movie savedMovie = movieRepository.save(movie);
        String posterUrl = baseUrl + "/file/" + uploadedFileName;
        return new MovieDto(savedMovie.getMovieId(),
                            savedMovie.getTitle(),
                            savedMovie.getDirector(),
                            savedMovie.getStudio(),
                            savedMovie.getMovieCast(),
                            savedMovie.getReleaseYear(),
                            savedMovie.getPoster(),
                            posterUrl);
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        Movie movie = movieRepository.findById(movieId)
                                     .orElseThrow(() -> new MovieNotFoundException("Movie Not " + "Found"));
        String posterUrl = baseUrl + "/file/" + movie.getPoster();
        return new MovieDto(movie.getMovieId(),
                            movie.getTitle(),
                            movie.getDirector(),
                            movie.getStudio(),
                            movie.getMovieCast(),
                            movie.getReleaseYear(),
                            movie.getPoster(),
                            posterUrl);

    }

    @Override
    public List<MovieDto> getAllMovies() {
        return getMovieDtos(movieRepository.findAll());
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        Movie mv = movieRepository.findById(movieId)
                                  .orElseThrow(() -> new MovieNotFoundException("Movie Not Found"));
        String fileName = mv.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        Movie movie = new Movie(mv.getMovieId(),
                                movieDto.getTitle(),
                                movieDto.getDirector(),
                                movieDto.getStudio(),
                                movieDto.getMovieCast(),
                                movieDto.getReleaseYear(),
                                movieDto.getPoster());
        Movie updatedMovie = movieRepository.save(movie);
        String posterUrl = baseUrl + "/file" + fileName;

        return new MovieDto(updatedMovie.getMovieId(),
                            updatedMovie.getTitle(),
                            updatedMovie.getDirector(),
                            updatedMovie.getStudio(),
                            updatedMovie.getMovieCast(),
                            updatedMovie.getReleaseYear(),
                            updatedMovie.getPoster(),
                            posterUrl);
    }

    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        Movie mv = movieRepository.findById(movieId)
                                  .orElseThrow(() -> new MovieNotFoundException("Movie Not Found"));
        Integer id = mv.getMovieId();
        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));
        movieRepository.delete(mv);
        return "Movie deleted with ID: " + id;
    }

    @Override
    public PaginatedMovie getAllMoviesPaginated(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<MovieDto> movieDtos = getMovieDtos(moviePages.getContent());

        return new PaginatedMovie(movieDtos,
                                  pageNumber,
                                  pageSize,
                                  moviePages.getTotalElements(),
                                  moviePages.getTotalPages(),
                                  moviePages.isLast());
    }

    @Override
    public PaginatedMovie getAllMoviesPaginatedWithSort(Integer pageNumber,
                                                        Integer pageSize,
                                                        String sortBy,
                                                        String dir) {

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Movie> moviePages = movieRepository.findAll(pageable);
        List<MovieDto> movieDtos = getMovieDtos(moviePages.getContent());

        return new PaginatedMovie(movieDtos,
                                  pageNumber,
                                  pageSize,
                                  moviePages.getTotalElements(),
                                  moviePages.getTotalPages(),
                                  moviePages.isLast());
    }

    private List<MovieDto> getMovieDtos(List<Movie> moviePages) {

        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie : moviePages) {
            String posterUrl = baseUrl + "/file/" + movie.getPoster();
            MovieDto dto = new MovieDto(movie.getMovieId(),
                                        movie.getTitle(),
                                        movie.getDirector(),
                                        movie.getStudio(),
                                        movie.getMovieCast(),
                                        movie.getReleaseYear(),
                                        movie.getPoster(),
                                        posterUrl);
            movieDtos.add(dto);
        }
        return movieDtos;
    }
}
