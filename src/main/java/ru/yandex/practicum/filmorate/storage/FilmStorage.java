package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    void delete(Long id);

    Optional<Film> findById(Long id);

    List<Film> findAll();

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> getPopularFilms(int count);

    List<Long> getFilmGenres(Long filmId);

    void setGenres(Long filmId, Set<Long> genreIds);
}