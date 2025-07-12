package ru.yandex.intershop.model.cart;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.intershop.model.item.Item;

import java.util.Objects;

@Table(name = "carts_items")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    @Id
    Long id;

    @Column("cart_id")
    Long cartId;

    @Column("item_id")
    Long itemId;

    @Setter
    @Column("item_count")
    Integer itemCount;

    @Setter
    @With
    @Transient
    Item item;

    @Override
    public boolean equals(Object other){
        if (other.getClass() != CartItem.class) return false;
        CartItem cartItem = (CartItem) other;
        return Objects.equals(cartItem.id, this.id);
    }

}
