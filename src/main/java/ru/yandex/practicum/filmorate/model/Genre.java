package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @NotNull(message = "ID жанра не может быть пустым")
    private Long id;

    @NotBlank(message = "Название жанра не может быть пустым")
    private String name;

}