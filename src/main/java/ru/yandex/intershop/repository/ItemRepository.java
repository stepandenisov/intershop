package ru.yandex.intershop.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.intershop.model.Item;
import ru.yandex.intershop.model.ItemDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "SELECT new ItemDto(item.id, item.title, item.description, item.price, cartItem.itemCount) "
            + " from Item item left join CartItem cartItem on cartItem.item.id = item.id left join Cart cart on cartItem.cart.id = cart.id"
            + " where item.id=:itemId")
    Optional<ItemDto> findByIdDto(@Param("itemId") Long itemId);

    @Query(value = "SELECT new ItemDto(item.id, item.title, item.description, item.price, cartItem.itemCount) "
            + " from Item item left join CartItem cartItem on cartItem.item.id = item.id left join Cart cart on cartItem.cart.id = cart.id"
            + " where (item.title like :search or item.description like :search)")
    List<ItemDto> findAllByTitleStartsWithOrDescriptionStartsWithDto(@Param("search") String search,
                                                                     Pageable page);

    Long countAllByTitleStartingWithOrDescriptionStartingWith(String title,
                                                              String description);

    @Query(value = "SELECT new ItemDto(item.id, item.title, item.description, item.price, cartItem.itemCount) "
            + " from Item item left join CartItem cartItem on cartItem.item.id = item.id left join Cart cart on cartItem.cart.id = cart.id ")
    Page<ItemDto> findAllDto(Pageable page);
}
