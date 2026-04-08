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
    @Size(max = 100)
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @PastOrPresent
    private LocalDate releaseDate;

    @NotNull
    @Min(1)
    @Max(1000)
    private Integer duration;

    private MpaRating mpa;
    private Set<Genre> genres;
    private Set<Long> likes = new HashSet<>();
}