package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaRatingStorage mpaRatingStorage;
    private final GenreStorage genreStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll().stream()
                .map(this::enrichFromDb)
                .toList();
    }

    public Film create(Film film) {
        film = enrichFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        film = enrichFilm(film);
        return filmStorage.update(film);
    }

    public void delete(Long id) {
        filmStorage.delete(id);
    }

    public Film getById(Long id) {
        Film film = filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: " + id));

        return enrichFromDb(film);
    }

    public void addLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId);
        userService.getById(userId);

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId);
        userService.getById(userId);

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film enrichFilm(Film film) {

        // MPA
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ValidationException("MPA обязателен");
        }

        // подтягиваем MPA с name из хранилища
        MpaRating mpa = mpaRatingStorage.getById(film.getMpa().getId());
        if (mpa == null) {
            throw new NotFoundException("MPA не найден с ID: " + film.getMpa().getId());
        }
        film.setMpa(mpa);

        // GENRES
        Set<Genre> genres = Optional.ofNullable(film.getGenres())
                .orElse(Collections.emptySet());

        film.setGenres(
                genres.stream()
                        .map(g -> genreStorage.getById(g.getId()))
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );

        // RELEASE DATE
        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28.12.1895");
        }

        return film;
    }

    private Film enrichFromDb(Film film) {
        List<Long> genreIds = filmStorage.getFilmGenres(film.getId());

        Set<Genre> genres = genreIds.stream()
                .map(genreStorage::getById)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        film.setGenres(genres);

        return film;
    }

    private Film getFilmOrThrow(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }
}