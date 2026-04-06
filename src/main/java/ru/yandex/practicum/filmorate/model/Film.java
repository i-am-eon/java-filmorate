package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 255, message = "Максимальная длина названия — 255 символов")
    private String name;

    @Size(max = 1000, message = "Максимальная длина описания — 1000 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @NotNull(message = "Длительность обязательна")
    @Min(value = 1, message = "Минимальная длительность — 1 минута")
    @Max(value = 1000, message = "Максимальная длительность — 1000 минут")
    private Integer duration;

    @NotNull(message = "MPA рейтинг обязателен")
    private Long mpaId;

    private Set<Long> genreIds = new HashSet<>();

    private Set<Long> likes = new HashSet<>();
}