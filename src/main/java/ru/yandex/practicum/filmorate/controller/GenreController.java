package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreStorage genreStorage;

    @GetMapping
    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    @GetMapping("/{id}")
    public Genre getById(@PathVariable Long id) {
        return genreStorage.getById(id);
    }
}