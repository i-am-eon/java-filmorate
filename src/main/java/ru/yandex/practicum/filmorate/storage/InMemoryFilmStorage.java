package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private long nextId = 1;

    @Override
    public Film create(Film film) {
        film.setId(getNextId());

        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }

        if (film.getMpa() == null) {
            throw new IllegalArgumentException("MPA рейтинг обязателен");
        }

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм не найден");
        }

        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }

        if (film.getMpa() == null) {
            throw new IllegalArgumentException("MPA рейтинг обязателен");
        }

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм не найден");
        }
        films.remove(id);
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film == null) throw new NotFoundException("Фильм не найден");

        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film == null) throw new NotFoundException("Фильм не найден");

        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    // Методы с Long id больше не нужны, можно выбросить UnsupportedOperationException
    @Override
    public List<Long> getFilmGenres(Long filmId) {
        throw new UnsupportedOperationException("Используйте film.getGenres() вместо genreIds");
    }

    @Override
    public void setGenres(Long filmId, Set<Long> genreIds) {
        throw new UnsupportedOperationException("Используйте film.setGenres() с объектами Genre");
    }

    private Long getNextId() {
        return nextId++;
    }
}