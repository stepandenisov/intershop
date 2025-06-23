package ru.yandex.intershop.model.order;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.yandex.intershop.model.item.Item;

@Entity
@Table(name = "orders_items")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;

    @Column(name = "item_count")
    Integer itemCount;

    @Column(name = "item_price")
    Double itemPrice;
}
