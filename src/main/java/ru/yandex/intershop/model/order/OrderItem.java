package ru.yandex.intershop.model.order;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.intershop.model.item.Item;

@Table(name = "orders_items")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    Long id;

    @Column("order_id")
    Long orderId;

    @Column("item_id")
    Long itemId;

    @Column("item_count")
    Integer itemCount;

    @Column("item_price")
    Double itemPrice;

    @Setter
    @With
    @Transient
    Item item;
}
