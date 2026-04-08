package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.model.MpaRating;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaRatingMapper {

    private MpaRatingMapper() {
    }

    public static MpaRating mapRowToMpaRating(ResultSet rs) throws SQLException {
        MpaRating mpaRating = new MpaRating();

        mpaRating.setId(rs.getLong("id"));
        mpaRating.setName(rs.getString("name"));

        return mpaRating;
    }
}