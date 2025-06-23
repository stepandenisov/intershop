package ru.yandex.intershop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
