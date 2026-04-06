package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class Genre {

    private Long id;

    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;
}