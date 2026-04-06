package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpaRating {
    private Long id;
    private String name;
}