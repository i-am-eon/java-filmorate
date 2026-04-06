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
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Фильм не найден");
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
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
        }
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        film.getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(
                        f2.getLikes().size(),
                        f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    @Override
    public List<Long> getFilmGenres(Long filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        return new ArrayList<>(film.getGenreIds());
    }

    @Override
    public void setGenres(Long filmId, Set<Long> genreIds) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        film.setGenreIds(new HashSet<>(genreIds));
    }

    private Long getNextId() {
        return nextId++;
    }
}