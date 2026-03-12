package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Попытка добавления пользователя с некорректной электронной почты");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Попытка добавления пользователя с некорректным логином");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getBirthday() == null) {
            log.warn("Попытка добавления пользователя без даты рождения");
            throw new ValidationException("Дата рождения должна быть указана");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка добавления пользователя из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Имя пустое, для отображения будет использован логин");
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан пользователь id={} email={}", user.getId(), user.getEmail());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {

        if (newUser.getId() == null) {
            log.warn("Попытка обновления пользователя без id");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("Попытка обновления несуществующего пользователя");
            throw new ValidationException("Пользователь не найден");
        }

        if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
            log.warn("Попытка обновления пользователя с некорректной электронной почтой");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
            log.warn("Попытка обновления пользователя с некорректным логином");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (newUser.getBirthday() == null) {
            log.warn("Попытка обновления пользователя без указания дня рождения");
            throw new ValidationException("Дата рождения должна быть указана");
        }

        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка обновления пользователя с датой рождения из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            log.info("Обновление пользователя без имени с отображением логина");
            newUser.setName(newUser.getLogin());
        }

        User oldUser = users.get(newUser.getId());

        oldUser.setName(newUser.getName());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setBirthday(newUser.getBirthday());
        log.info("Обновлен пользователь id={} email={}", oldUser.getId(), oldUser.getEmail());
        return oldUser;
    }

    private int getNextId() {
        return users.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
    }
}