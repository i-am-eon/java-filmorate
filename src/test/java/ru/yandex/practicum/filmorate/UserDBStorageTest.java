package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDBStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserDBStorageTest {

    @Autowired
    private UserDBStorage userDBStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User testUser;

    @BeforeEach
    void setup() {

        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM films_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM app_users");
        jdbcTemplate.execute("ALTER TABLE app_users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");

        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testuser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateUser() {
        User createdUser = userDBStorage.create(testUser);

        assertNotNull(createdUser.getId(), "Id пользователя должен быть присвоен");
        assertEquals("testuser", createdUser.getLogin());
    }

    @Test
    void testFindUserById() {
        User createdUser = userDBStorage.create(testUser);

        Optional<User> foundUser = userDBStorage.findById(createdUser.getId());
        assertTrue(foundUser.isPresent(), "Пользователь должен существовать");
        assertEquals(createdUser.getId(), foundUser.get().getId());
    }

    @Test
    void testUpdateUser() {
        User createdUser = userDBStorage.create(testUser);

        createdUser.setName("Updated Name");
        User updatedUser = userDBStorage.update(createdUser);

        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void testGetAllUsers() {
        User createdUser = userDBStorage.create(testUser);

        List<User> users = userDBStorage.findAll();
        assertFalse(users.isEmpty(), "Список пользователей не должен быть пустым");
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(createdUser.getId())));
    }

    @Test
    void testDeleteUser() {
        User createdUser = userDBStorage.create(testUser);

        userDBStorage.delete(createdUser.getId());
        Optional<User> deletedUser = userDBStorage.findById(createdUser.getId());

        assertTrue(deletedUser.isEmpty(), "Пользователь должен быть удален");
    }
}