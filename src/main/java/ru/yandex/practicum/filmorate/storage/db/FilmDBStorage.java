package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDBStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpaId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id=?";

        int rows = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaId(),
                film.getId());

        if (rows == 0) {
            throw new NotFoundException("Фильм не найден");
        }

        return film;
    }

    @Override
    public void delete(Long id) {
        int rows = jdbcTemplate.update("DELETE FROM films WHERE id=?", id);
        if (rows == 0) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT * FROM films WHERE id=?";
        return jdbcTemplate.query(sql, filmRowMapper, id).stream().findFirst();
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query("SELECT * FROM films", filmRowMapper);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update("INSERT INTO likes(film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = """
            SELECT f.*
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC
            LIMIT ?
            """;

        return jdbcTemplate.query(sql, filmRowMapper, count);
    }

    @Override
    public List<Long> getFilmGenres(Long filmId) {
        return jdbcTemplate.queryForList(
                "SELECT genre_id FROM film_genres WHERE film_id=?",
                Long.class,
                filmId
        );
    }

    @Override
    public void setGenres(Long filmId, Set<Long> genreIds) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", filmId);

        for (Long genreId : genreIds) {
            jdbcTemplate.update(
                    "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)",
                    filmId,
                    genreId
            );
        }
    }
}