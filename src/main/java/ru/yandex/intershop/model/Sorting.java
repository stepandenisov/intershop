package ru.yandex.intershop.model;

import lombok.Getter;

public enum Sorting {
    NO("id"),
    ALPHA("title"),
    PRICE("price");

    public final String field;

    Sorting(String field) {
        this.field = field;
    }
}
