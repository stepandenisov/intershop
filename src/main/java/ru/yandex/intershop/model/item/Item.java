package ru.yandex.intershop.model.item;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.intershop.model.order.Order;

import java.io.Serializable;
import java.util.Objects;

@Table(name = "items")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Item implements Serializable {

    @Id
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
