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
        if (film.getMpa() == null) {
            throw new IllegalArgumentException("MPA рейтинг обязателен");
        }

        String sql = "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId()); // берём id из объекта MpaRating
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        // Сохраняем жанры, если есть
        if (film.getGenres() != null) {
            for (var genre : film.getGenres()) {
                jdbcTemplate.update(
                        "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)",
                        film.getId(),
                        genre.getId()
                );
            }
        }

        return film;
    }

    @Override
    public Film update(Film film) {
        if (film.getMpa() == null) {
            throw new IllegalArgumentException("MPA рейтинг обязателен");
        }

        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id=?";

        int rows = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (rows == 0) {
            throw new NotFoundException("Фильм не найден");
        }

        // Обновляем жанры: сначала удаляем все, затем вставляем новые
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id=?", film.getId());

        if (film.getGenres() != null) {
            for (var genre : film.getGenres()) {
                jdbcTemplate.update(
                        "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)",
                        film.getId(),
                        genre.getId()
                );
            }
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
        String sql = """
        SELECT f.*, m.name AS mpa_name
        FROM films f
        JOIN mpa m ON f.mpa_id = m.id
        WHERE f.id = ?
    """;

        return jdbcTemplate.query(sql, filmRowMapper, id)
                .stream()
                .findFirst();
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
        String sql = String.format("""
        SELECT f.*, COALESCE(l.likes_count, 0) AS likes_count
        FROM films f
        LEFT JOIN (
            SELECT film_id, COUNT(user_id) AS likes_count
            FROM likes
            GROUP BY film_id
        ) l ON f.id = l.film_id
        ORDER BY likes_count DESC
        FETCH FIRST %d ROWS ONLY
    """, count);

        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public List<Long> getFilmGenres(Long filmId) {
        return jdbcTemplate.queryForList(
                "SELECT genre_id FROM films_genres WHERE film_id=?",
                Long.class,
                filmId
        );
    }

    @Override
    public void setGenres(Long filmId, Set<Long> genreIds) {
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id=?", filmId);

        for (Long genreId : genreIds) {
            jdbcTemplate.update(
                    "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)",
                    filmId,
                    genreId
            );
        }
    }
}