package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class MpaRatingDBStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<MpaRating> getAll() {
        String sql = "SELECT id, name FROM mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new MpaRating(rs.getLong("id"), rs.getString("name"))
        );
    }

    @Override
    public MpaRating getById(Long id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";

        List<MpaRating> result = jdbcTemplate.query(sql, (rs, rowNum) ->
                new MpaRating(rs.getLong("id"), rs.getString("name")), id);

        if (result.isEmpty()) {
            throw new NotFoundException("MPA рейтинг не найден: " + id);
        }

        return result.get(0);
    }
}