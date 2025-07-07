package ru.yandex.intershop.model.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.OrderItem;

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

    @Override
    public boolean equals(Object other){
        if (other.getClass() != CartItem.class) return false;
        CartItem cartItem = (CartItem) other;
        return Objects.equals(cartItem.id, this.id);
    }

}
