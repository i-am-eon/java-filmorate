package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserService userService;

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAll() {
        log.info("Получение списка фильмов");
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        log.info("Добавление фильма - {}", film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        log.info("Изменение фильма - {}", film);
        return filmStorage.update(film);
    }

    public void delete(Long id) {
        log.info("Удаление фильма id={}", id);
        filmStorage.delete(id);
    }

    public Film getById(Long id) {
        log.info("Получение фильма id={}", id);
        return getFilmOrThrow(id);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Добавление лайка фильму id={} от пользователя id={}", filmId, userId);

        Film film = getFilmOrThrow(filmId);
        User user = userService.getById(userId);

        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
            filmStorage.update(film);
        } else {
            log.warn("Пользователь id={} уже поставил лайк фильму id={}", userId, filmId);
        }
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка у фильма id={} от пользователя id={}", filmId, userId);

        Film film = getFilmOrThrow(filmId);
        User user = userService.getById(userId);

        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
            filmStorage.update(film);
        } else {
            log.warn("Пользователь id={} не ставил лайк фильму id={}", userId, filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получение {} популярных фильмов", count);
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }

    private Film getFilmOrThrow(Long id) {
        Film film = filmStorage.getById(id);

        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        return film;
    }
}