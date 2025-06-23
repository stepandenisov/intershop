package ru.yandex.intershop.model.image;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.intershop.model.order.Order;

import java.util.Objects;

@Entity
@Table(name="images")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "item_id")
    private Long itemId;

    @Lob
    @Column(name = "image")
    private byte[] imageBytes;

    @Override
    public boolean equals(Object other){
        if (other.getClass() != Image.class) return false;
        Image image = (Image) other;
        return Objects.equals(image.id, this.id);
    }
}
