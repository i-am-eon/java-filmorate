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
        if (film.getMpa() == null || film.getMpa().getId() == null) {
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
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        saveGenres(film);

        return getByIdWithGenres(film.getId());
    }

    @Override
    public Film update(Film film) {
        if (film.getMpa() == null || film.getMpa().getId() == null) {
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

        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id=?", film.getId());
        saveGenres(film);

        return getByIdWithGenres(film.getId());
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
                .findFirst()
                .map(this::enrichFilm);
    }

    @Override
    public List<Film> findAll() {
        String sql = """
                SELECT f.*, m.name AS mpa_name
                FROM films f
                JOIN mpa m ON f.mpa_id = m.id
                """;

        return jdbcTemplate.query(sql, filmRowMapper)
                .stream()
                .map(this::enrichFilm)
                .toList();
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        getByIdWithGenres(filmId); // проверка
        jdbcTemplate.update("INSERT INTO likes(film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        getByIdWithGenres(filmId); // проверка
        jdbcTemplate.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = String.format("""
                SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS likes_count
                FROM films f
                JOIN mpa m ON f.mpa_id = m.id
                LEFT JOIN likes l ON f.id = l.film_id
                GROUP BY f.id, m.name
                ORDER BY likes_count DESC
                LIMIT %d
                """, count);

        return jdbcTemplate.query(sql, filmRowMapper)
                .stream()
                .map(this::enrichFilm)
                .toList();
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

        if (genreIds != null && !genreIds.isEmpty()) {
            String sql = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";

            jdbcTemplate.batchUpdate(sql, genreIds, genreIds.size(),
                    (ps, genreId) -> {
                        ps.setLong(1, filmId);
                        ps.setLong(2, genreId);
                    });
        }
    }

    private Film getByIdWithGenres(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";

        var genres = film.getGenres().stream()
                .filter(g -> g != null && g.getId() != null)
                .toList();

        jdbcTemplate.batchUpdate(sql, genres, genres.size(),
                (ps, genre) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                });
    }

    private Film enrichFilm(Film film) {
        var genreIds = getFilmGenres(film.getId());

        film.setGenres(
                genreIds.stream()
                        .map(id -> new ru.yandex.practicum.filmorate.model.Genre(id, null))
                        .collect(java.util.stream.Collectors.toCollection(java.util.LinkedHashSet::new))
        );

        return film;
    }
}