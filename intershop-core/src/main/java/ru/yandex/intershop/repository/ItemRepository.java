package ru.yandex.intershop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;


@Repository
public interface ItemRepository extends ReactiveSortingRepository<Item, Long>, ReactiveCrudRepository<Item, Long> {

    @Query(value = """
            SELECT items.id, items.title, items.description, items.price, cart.item_count as count
            from items
            left join (select carts_items.item_id as item_id, carts_items.item_count as item_count, carts.user_id from carts_items
            left join carts on carts_items.cart_id = carts.id
            where carts.user_id = :userId) as cart
            on cart.item_id = items.id
            where items.id=:itemId
            """)
    Mono<ItemDto> findByIdDtoJoinCountOnUserId(@Param("itemId") Long itemId, @Param("userId") Long userId);

    @Query(value = """
            SELECT items.id as id, items.title as title, items.description, items.price as price, cart.item_count as count
            from items
            left join (select carts_items.item_id as item_id, carts_items.item_count as item_count, carts.user_id from carts_items
            left join carts on carts_items.cart_id = carts.id
            where carts.user_id = :userId) as cart
            on cart.item_id = items.id
            where items.title like :search OR items.description like :search
            ORDER BY :#{[0].sort.toString().replace(":", "")} IS NULL
            LIMIT :#{[0].pageSize} OFFSET :#{[0].offset}
            """)
    Flux<ItemDto> findAllByTitleStartsWithOrDescriptionStartsWithDtoSortByOffsetLimit(Pageable pageable,
                                                                                      @Param("search") String search,
                                                                                      @Param("userId") Long userId);

    Mono<Long> countAllByTitleStartingWithOrDescriptionStartingWith(String title,
                                                                    String description);

    @Query(value = """
            SELECT items.id as id, items.title as title, items.description, items.price as price, cart.item_count as count
            from items
            left join (select carts_items.item_id as item_id, carts_items.item_count as item_count, carts.user_id from carts_items
            left join carts on carts_items.cart_id = carts.id
            where carts.user_id = :userId) as cart
            on cart.item_id = items.id
            ORDER BY :#{[0].sort.toString().replace(":", "")} IS NULL
            LIMIT :#{[0].pageSize} OFFSET :#{[0].offset}
            """)
    Flux<ItemDto> findAllDtoSortByOffsetLimit(Pageable pageable, @Param("userId") Long userId);

}
