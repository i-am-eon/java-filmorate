package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        log.info("Создание пользователя");
        return userStorage.create(user);
    }

    public User update(User user) {
        log.info("Изменение пользователя");
        return userStorage.update(user);
    }

    public void delete(Long id) {
        log.info("Удаление пользователя id={}", id);
        userStorage.delete(id);
    }

    public User getById(Long id) {
        log.info("Получение пользователя id={}", id);
        return userStorage.getById(id);
    }

    public Collection<User> findAll() {
        log.info("Получение списка всех пользователей");
        return userStorage.findAll();
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь id={} добавляет в друзья пользователя id={}", userId, friendId);

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.info("Пользователь id={} удаляет из друзей пользователя id={}", userId, friendId);

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) {
        log.info("Получение списка друзей пользователя id={}", userId);
        User user = getUserOrThrow(userId);

        return user.getFriends().stream()
                .map(this::getUserOrThrow)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Получение списка общих друзей пользователя id={} и пользователя id={}", userId, otherId);
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(userStorage::getById)
                .toList();
    }

    private User getUserOrThrow(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }
}