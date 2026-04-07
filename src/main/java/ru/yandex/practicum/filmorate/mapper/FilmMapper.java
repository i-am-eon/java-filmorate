package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class FilmMapper {

    public static Film mapRowToFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        // MPA
        MpaRating mpa = new MpaRating();
        mpa.setId(rs.getLong("mpa_id"));
        mpa.setName(rs.getString("mpa_name")); // <- важно, чтобы SQL делал JOIN и выдавал mpa_name
        film.setMpa(mpa);

        // жанры (если есть)
        film.setGenres(new HashSet<>()); // можно оставить пустым, подтягивать позже

        return film;
    }
}