package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaRatingDBStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDBStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmDBStorage.class,
        FilmRowMapper.class,
        MpaRatingDBStorage.class,
        UserDBStorage.class,
        UserRowMapper.class
})
class FilmDBStorageTest {

    @Autowired
    private FilmDBStorage filmDBStorage;

    @Autowired
    private UserDBStorage userDBStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Film testFilm;
    private User user1;
    private User user2;

    @BeforeEach
    void setup() {

        // ---- Очистка таблиц ----
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM films_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM app_users");

        // Создаём MPA
        MpaRating mpa = new MpaRating();
        mpa.setId(1L);

        // Создаём фильм
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(120);
        testFilm.setMpa(mpa);

        // Создаём пользователей
        user1 = new User();
        user1.setEmail("user1@mail.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        user1 = userDBStorage.create(user1);

        user2 = new User();
        user2.setEmail("user2@mail.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1992, 2, 2));
        user2 = userDBStorage.create(user2);
    }

    @Test
    void testFilmCRUD() {
        // CREATE
        Film createdFilm = filmDBStorage.create(testFilm);
        assertThat(createdFilm.getId()).isNotNull();

        // FIND BY ID
        Film foundFilm = filmDBStorage.findById(createdFilm.getId()).orElseThrow();
        assertThat(foundFilm.getName()).isEqualTo("Test Film");

        // UPDATE
        createdFilm.setName("Updated Film");
        createdFilm.setGenres(Set.of(
                new Genre(1L, "Комедия"),
                new Genre(2L, "Драма")
        ));
        Film updatedFilm = filmDBStorage.update(createdFilm);
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");

        // Проверка жанров
        List<Long> genres = filmDBStorage.getFilmGenres(updatedFilm.getId());
        assertThat(genres).containsExactlyInAnyOrder(1L, 2L);

        // GET ALL
        List<Film> allFilms = filmDBStorage.findAll();
        assertThat(allFilms).anyMatch(f -> f.getId().equals(updatedFilm.getId()));

        // ADD LIKE
        filmDBStorage.addLike(updatedFilm.getId(), user1.getId());
        filmDBStorage.addLike(updatedFilm.getId(), user2.getId());
        assertThat(filmDBStorage.getPopularFilms(1))
                .extracting(Film::getId)
                .containsExactly(updatedFilm.getId());

        // REMOVE LIKE
        filmDBStorage.removeLike(updatedFilm.getId(), user1.getId());
        assertThat(filmDBStorage.getPopularFilms(1))
                .extracting(Film::getId)
                .containsExactly(updatedFilm.getId());

        // DELETE
        filmDBStorage.delete(updatedFilm.getId());
        assertThat(filmDBStorage.findById(updatedFilm.getId())).isEmpty();
    }
}