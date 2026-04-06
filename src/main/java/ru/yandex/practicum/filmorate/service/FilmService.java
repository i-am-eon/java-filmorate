package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void delete(Long id) {
        filmStorage.delete(id);
    }

    public Film getById(Long id) {
        return getFilmOrThrow(id);
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

    private Film getFilmOrThrow(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }
}