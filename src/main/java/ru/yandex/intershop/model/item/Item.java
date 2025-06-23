package ru.yandex.intershop.model.item;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.intershop.model.order.Order;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "items")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Double price;

    @Override
    public boolean equals(Object other){
        if (other.getClass() != Item.class) return false;
        Item item = (Item) other;
        return Objects.equals(item.id, this.id);
    }
}
