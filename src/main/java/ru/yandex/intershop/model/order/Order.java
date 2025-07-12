package ru.yandex.intershop.model.order;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(name = "orders")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    private Long id;

    private Double total;

    @Setter
    @With
    @Transient
    private List<OrderItem> orderItems = new ArrayList<>();


    @Override
    public boolean equals(Object other){
        if (other.getClass() != Order.class) return false;
        Order order = (Order) other;
        return Objects.equals(order.id, this.id);
    }
}
