package ru.yandex.intershop.model.image;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.yandex.intershop.model.order.Order;

import java.util.Objects;

@Table(name="images")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    private Long id;

    @Setter
    @Column("item_id")
    private Long itemId;

    @Column("image")
    private Byte[] imageBytes;

    @Override
    public boolean equals(Object other){
        if (other.getClass() != Image.class) return false;
        Image image = (Image) other;
        return Objects.equals(image.id, this.id);
    }
}
