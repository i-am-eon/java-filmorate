package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryMpaRatingStorage implements MpaRatingStorage {

    private final Map<Long, MpaRating> mpa = new HashMap<>();

    public InMemoryMpaRatingStorage() {
        // Используем значения по US системе, чтобы тесты на PG-13 проходили
        mpa.put(1L, new MpaRating(1L, "G"));
        mpa.put(2L, new MpaRating(2L, "PG"));
        mpa.put(3L, new MpaRating(3L, "PG-13"));
        mpa.put(4L, new MpaRating(4L, "R"));
        mpa.put(5L, new MpaRating(5L, "NC-17"));
    }

    public List<MpaRating> getAll() {
        return new ArrayList<>(mpa.values());
    }

    @Override
    public MpaRating getById(Long id) {
        MpaRating rating = mpa.get(id);

        if (rating == null) {
            throw new NotFoundException("MPA не найден: " + id);
        }

        return rating;
    }
}