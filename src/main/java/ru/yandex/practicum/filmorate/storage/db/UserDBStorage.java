package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDBStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    @Override
    public User create(User user) {
        String sql = "INSERT INTO app_users(email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE app_users SET email=?, login=?, name=?, birthday=? WHERE id=?";

        int rows = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());

        if (rows == 0) {
            throw new NotFoundException("Пользователь не найден");
        }

        return user;
    }

    @Override
    public void delete(Long id) {
        int rows = jdbcTemplate.update("DELETE FROM app_users WHERE id=?", id);
        if (rows == 0) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM app_users WHERE id=?";
        return jdbcTemplate.query(sql, userRowMapper, id).stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM app_users", userRowMapper);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        jdbcTemplate.update("INSERT INTO friendships(user_id, friend_id) VALUES (?, ?)", userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update("DELETE FROM friendships WHERE user_id=? AND friend_id=?", userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = """
            SELECT u.*
            FROM app_users u
            JOIN friendships f ON u.id = f.friend_id
            WHERE f.user_id = ?
            """;

        return jdbcTemplate.query(sql, userRowMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        String sql = """
            SELECT u.*
            FROM app_users u
            JOIN friendships f1 ON u.id = f1.friend_id
            JOIN friendships f2 ON u.id = f2.friend_id
            WHERE f1.user_id = ?
              AND f2.user_id = ?
            """;

        return jdbcTemplate.query(sql, userRowMapper, userId, otherId);
    }
}