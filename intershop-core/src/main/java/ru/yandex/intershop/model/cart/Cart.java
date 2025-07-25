package ru.yandex.intershop.model.cart;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(name = "carts")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Cart {

    @Id
    private Long id;

    @Setter
    private Double total;

    @Column("user_id")
    private Long userId;

    @Setter
    @With
    @Transient
    private List<CartItem> cartItems = new ArrayList<>();


    @Override
    public boolean equals(Object other){
        if (other.getClass() != Cart.class) return false;
        Cart cart = (Cart) other;
        return Objects.equals(cart.id, this.id);
    }
}
