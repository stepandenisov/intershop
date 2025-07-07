package ru.yandex.intershop.model.cart;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.intershop.model.image.Image;

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

    @Setter
    @With
    @Transient
//    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CartItem> cartItems = new ArrayList<>();


    @Override
    public boolean equals(Object other){
        if (other.getClass() != Cart.class) return false;
        Cart cart = (Cart) other;
        return Objects.equals(cart.id, this.id);
    }
}
