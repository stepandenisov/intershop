package ru.yandex.intershop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;


@Repository
public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {

    @Query(value = """
            SELECT items.id, items.title, items.description, items.price, carts_items.item_count as count
            from items
            left join carts_items on carts_items.item_id = items.id
            where items.id=:itemId
            """)
    Mono<ItemDto> findByIdDto(@Param("itemId") Long itemId);

    @Query(value = """
            SELECT items.id, items.title, items.description, items.price, carts_items.item_count as count
            from items
            left join carts_items on carts_items.item_id = items.id
            where (items.title like :search or items.description like :search)
            """)
    Flux<ItemDto> findAllByTitleStartsWithOrDescriptionStartsWithDto(@Param("search") String search,
                                                                     Pageable page);

    Mono<Long> countAllByTitleStartingWithOrDescriptionStartingWith(String title,
                                                              String description);

    @Query(value = """
            SELECT items.id, items.title, items.description, items.price, carts_items.item_count
            from items
            left join carts_items on carts_items.item_id = items.id
            """)
    Flux<ItemDto> findAllDto(Pageable page);

}
