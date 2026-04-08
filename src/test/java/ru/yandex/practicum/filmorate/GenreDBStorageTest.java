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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDBStorage;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(GenreDBStorage.class)
class GenreDBStorageTest {

    @Autowired
    private final GenreDBStorage genreDBStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        // Очистка таблиц
        jdbcTemplate.execute("DELETE FROM films_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM app_users");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE app_users ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void testGetAllGenres() {
        List<Genre> genres = genreDBStorage.getAll();

        assertThat(genres)
                .isNotNull()
                .isNotEmpty();

        assertThat(genres.get(0))
                .hasFieldOrProperty("id")
                .hasFieldOrProperty("name");
    }

    @Test
    void testGetGenreById() {
        Genre first = genreDBStorage.getAll().get(0);

        Genre genreById = genreDBStorage.getById(first.getId());

        assertThat(genreById)
                .isNotNull()
                .extracting(Genre::getId, Genre::getName)
                .containsExactly(first.getId(), first.getName());
    }

    @Test
    void testGetGenreByInvalidId() {
        assertThatThrownBy(() -> genreDBStorage.getById(999L))
                .isInstanceOf(NotFoundException.class);
    }
}