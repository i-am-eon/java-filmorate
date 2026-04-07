package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.db.MpaRatingDBStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(MpaRatingDBStorage.class)
class MpaRatingDBStorageTest {

    @Autowired
    private final MpaRatingDBStorage mpaRatingDBStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM app_users");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE app_users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void testGetAllMpaRatings() {
        List<MpaRating> ratings = mpaRatingDBStorage.getAll();

        assertThat(ratings)
                .isNotNull()
                .isNotEmpty();

        assertThat(ratings.get(0))
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("name");
    }

    @Test
    void testGetMpaRatingById() {
        MpaRating first = mpaRatingDBStorage.getAll().get(0);

        MpaRating ratingById = mpaRatingDBStorage.getById(first.getId());

        assertThat(ratingById)
                .isNotNull()
                .extracting(MpaRating::getId, MpaRating::getName)
                .containsExactly(first.getId(), first.getName());
    }

    @Test
    void testGetMpaRatingByInvalidId() {
        assertThatThrownBy(() -> mpaRatingDBStorage.getById(999L))
                .isInstanceOf(NotFoundException.class);
    }
}