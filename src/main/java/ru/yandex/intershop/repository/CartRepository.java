package ru.yandex.intershop.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.cart.Cart;


@Repository
public interface CartRepository extends ReactiveCrudRepository<Cart, Long>{

    Mono<Cart> findById(Long id);


//        return databaseClient.sql("""
//                        SELECT carts.id, carts.total, items_joined.item_ids, items_joined.cart_item_ids, items_joined.item_counts from carts
//                        LEFT outer join  (
//                                           select carts_items.cart_id as id, array_agg(carts_items.item_id) item_ids, array_agg(carts_items.id) cart_item_ids, array_agg(carts_items.item_count) item_counts
//                                           from carts_items
//                                           join carts on carts_items.cart_id = carts.id
//                                           group by carts_items.cart_id
//                                           ) items_joined using (id)
//                        WHERE carts.id = :id
//                        """)
//                .bind("id", id)
//                .map((row, metadata) -> {
//                    Long[] itemIds = row.get("item_ids", Long[].class);
//                    Integer[] itemCounts = row.get("item_counts", Integer[].class);
//                    Long[] ids = row.get("cart_item_ids", Long[].class);
//                    List<CartItem> cartItems = new ArrayList<>();
//
//                    if (ids != null) {
//                        for (int i = 0; i < ids.length; i++) {
//                            cartItems.add(new CartItem(ids[i], id, itemIds[i], itemCounts[i]));
//                        }
//                    }
//
//                    return new Cart(
//                            row.get("id", Long.class),
//                            row.get("total", Double.class),
//                            cartItems);
//                })
//                .one();

}
