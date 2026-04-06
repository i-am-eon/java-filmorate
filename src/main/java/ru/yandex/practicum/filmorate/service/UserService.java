package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        getUserOrThrow(user.getId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.update(user);
    }

    public void delete(Long id) {
        userStorage.delete(id);
    }

    public User getById(Long id) {
        return getUserOrThrow(id);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя");
        }

        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        getUserOrThrow(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        getUserOrThrow(userId);
        getUserOrThrow(otherId);

        return userStorage.getCommonFriends(userId, otherId);
    }

    private User getUserOrThrow(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}