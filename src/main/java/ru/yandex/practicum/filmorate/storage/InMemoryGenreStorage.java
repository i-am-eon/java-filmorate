package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryGenreStorage implements GenreStorage {
    private final Map<Long, Genre> genres = new HashMap<>();

    public InMemoryGenreStorage() {
        genres.put(1L, new Genre(1L, "Комедия"));
        genres.put(2L, new Genre(2L, "Драма"));
        genres.put(3L, new Genre(3L, "Мультфильм"));
        genres.put(4L, new Genre(4L, "Триллер"));
        genres.put(5L, new Genre(5L, "Документальный"));
        genres.put(6L, new Genre(6L, "Боевик"));
    }

    public List<Genre> getAll() {
        return new ArrayList<>(genres.values());
    }

    @Override
    public Genre getById(Long id) {
        Genre genre = genres.get(id);

        if (genre == null) {
            throw new NotFoundException("Жанр не найден: " + id);
        }

        return genre;
    }
}