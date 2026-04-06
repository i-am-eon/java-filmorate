package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private Long id;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Логин обязателен")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения обязательна")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}