package ru.yandex.intershop.model.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ItemDto {

    private Long id;

    private String title;

    private String description;

    private Double price;

    private Integer count = null;
}
