package ru.yandex.intershop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@IdClass(Item.class)
public class ItemDto {

    @Id
    private Long id;

    private String title;

    private String description;

    private Double price;

    private Integer count = 0;
}
