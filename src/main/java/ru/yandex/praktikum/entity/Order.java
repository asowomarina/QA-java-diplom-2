package ru.yandex.praktikum.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class Order {
    private List<String> ingredients;

    public Order() {
        ingredients = new ArrayList<>();
    }
}