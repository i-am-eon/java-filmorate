package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Film;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;

public class FilmMapper {

    private FilmMapper() {
    }

    public static Film mapRowToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        rs.getLong("mpa_id");
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        java.sql.Date releaseDate = rs.getDate("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDate());
        }

        film.setDuration(rs.getInt("duration"));
        film.setMpaId(rs.getLong("mpa_id"));
        film.setGenreIds(new HashSet<>());
        film.setLikes(new HashSet<>());

        return film;
    }
}