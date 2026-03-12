package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void shouldThrowExceptionWhenEmailInvalid() {
        User user = new User();
        user.setEmail("invalidEmail");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000,1,1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldThrowExceptionWhenLoginEmptyOrContainsSpace() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("with space");
        user.setBirthday(LocalDate.of(2000,1,1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldThrowExceptionWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldSetLoginAsNameWhenNameEmpty() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000,1,1));
        user.setName("");

        User created = userController.create(user);
        assertEquals("login", created.getName());
    }

    @Test
    void shouldCreateUserWhenValid() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2000,1,1));
        user.setName("User");

        User created = userController.create(user);

        assertNotNull(created.getId());
        assertEquals("User", created.getName());
        assertTrue(created.getId() > 0);
    }

    @Test
    void shouldAssignIncrementalIds() {
        User user1 = new User();
        user1.setEmail("u1@mail.com");
        user1.setLogin("u1");
        user1.setBirthday(LocalDate.of(2000,1,1));

        User user2 = new User();
        user2.setEmail("u2@mail.com");
        user2.setLogin("u2");
        user2.setBirthday(LocalDate.of(2000,1,1));

        User created1 = userController.create(user1);
        User created2 = userController.create(user2);

        assertTrue(created2.getId() > created1.getId());
    }

}