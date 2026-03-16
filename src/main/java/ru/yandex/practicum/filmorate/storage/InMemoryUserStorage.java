package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь не найден");
        }

        users.put(user.getId(), user);
        log.info("Изменен пользователь id={}", user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {

        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }

        users.remove(id);

        log.info("Удалён пользователь id={}", id);
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    private long getNextId() {
        return nextId++;
    }
}