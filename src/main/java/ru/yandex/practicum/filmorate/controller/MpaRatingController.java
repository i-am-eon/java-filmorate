package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaRatingController {

    private final MpaRatingStorage mpaRatingStorage;

    @GetMapping
    public List<MpaRating> getAll() {
        return mpaRatingStorage.getAll();
    }

    @GetMapping("/{id}")
    public MpaRating getById(@PathVariable Long id) {
        return mpaRatingStorage.getById(id);
    }
}