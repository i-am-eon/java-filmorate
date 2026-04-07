package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDBStorage;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDBStorage.class, UserRowMapper.class})
class UserDBStorageTest {

    private final UserDBStorage userDBStorage;
    private final JdbcTemplate jdbcTemplate;

    private User testUser;

    @BeforeEach
    void setup() {
        // Очистка таблиц и сброс автоинкрементов
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM films_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM app_users");
        jdbcTemplate.execute("ALTER TABLE app_users ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN id RESTART WITH 1");

        // Подготовка пользователя
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setLogin("testuser");
        testUser.setName("Test User");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testCreateUser() {
        User createdUser = userDBStorage.create(testUser);
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getLogin()).isEqualTo("testuser");
    }

    @Test
    void testFindUserById() {
        User createdUser = userDBStorage.create(testUser);
        Optional<User> foundUser = userDBStorage.findById(createdUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(createdUser.getId());
    }

    @Test
    void testUpdateUser() {
        User createdUser = userDBStorage.create(testUser);
        createdUser.setName("Updated Name");
        User updatedUser = userDBStorage.update(createdUser);
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }

    @Test
    void testGetAllUsers() {
        User createdUser = userDBStorage.create(testUser);
        List<User> users = userDBStorage.findAll();
        assertThat(users).isNotEmpty();
        assertThat(users).anyMatch(u -> u.getId().equals(createdUser.getId()));
    }

    @Test
    void testDeleteUser() {
        User createdUser = userDBStorage.create(testUser);
        userDBStorage.delete(createdUser.getId());
        Optional<User> deletedUser = userDBStorage.findById(createdUser.getId());
        assertThat(deletedUser).isEmpty();
    }
}