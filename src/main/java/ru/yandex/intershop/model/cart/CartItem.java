package ru.yandex.intershop.model.cart;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.OrderItem;

import java.util.Objects;

@Entity
@Table(name = "carts_items")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "cart_id")
    Cart cart;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "item_id")
    Item item;

    @Setter
    @Column(name = "item_count")
    Integer itemCount;

    @Override
    public boolean equals(Object other){
        if (other.getClass() != CartItem.class) return false;
        CartItem cartItem = (CartItem) other;
        return Objects.equals(cartItem.id, this.id);
    }

}
