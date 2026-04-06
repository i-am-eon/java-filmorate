package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreMapper {

    private GenreMapper() {
    }

    public static Genre mapRowToGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();

        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("name"));

        return genre;
    }
}