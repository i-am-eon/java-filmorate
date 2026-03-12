package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Test");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("x".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldCreateFilmWhenDescriptionOnBorder() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("x".repeat(200));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        Film created = filmController.create(film);
        assertNotNull(created.getDescription());
        assertEquals(200, created.getDescription().length());
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateTooEarly() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(100);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldCreateFilmWhenReleaseDateOnBorder() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(100);

        Film created = filmController.create(film);
        assertNotNull(created.getReleaseDate());
        assertEquals(LocalDate.of(1895, 12, 28), created.getReleaseDate());
    }

    @Test
    void shouldThrowExceptionWhenDurationNegativeOrZero() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldCreateFilmWhenValid() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Film created = filmController.create(film);
        assertNotNull(created.getId());
        assertEquals("Film", created.getName());
        assertTrue(created.getId() > 0);
    }

    @Test
    void shouldAssignIncrementalIds() {
        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Desc");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(100);

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Desc");
        film2.setReleaseDate(LocalDate.of(2000, 1, 1));
        film2.setDuration(100);

        Film created1 = filmController.create(film1);
        Film created2 = filmController.create(film2);

        assertTrue(created2.getId() > created1.getId());
    }

}