package ru.yandex.intershop.configuration;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.ItemDto;

import java.util.List;


@Configuration
public class RedisConfiguration {

    @Bean
    public ReactiveRedisTemplate<String, Image> reactiveImageRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        RedisSerializer<String> keySerializer = new StringRedisSerializer();

        ObjectMapper objectMapper = new ObjectMapper();
        Jackson2JsonRedisSerializer<Image> serializer = new Jackson2JsonRedisSerializer<>(Image.class);
        serializer.setObjectMapper(objectMapper);

        RedisSerializationContext<String, Image> context = RedisSerializationContext
                .<String, Image>newSerializationContext(keySerializer)
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, ItemDto> reactiveItemDtoRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        RedisSerializer<String> keySerializer = new StringRedisSerializer();

        ObjectMapper objectMapper = new ObjectMapper();
        Jackson2JsonRedisSerializer<ItemDto> serializer = new Jackson2JsonRedisSerializer<>(ItemDto.class);
        serializer.setObjectMapper(objectMapper);

        RedisSerializationContext<String, ItemDto> context = RedisSerializationContext
                .<String, ItemDto>newSerializationContext(keySerializer)
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }


    @Bean
        public ReactiveRedisTemplate<String, List<ItemDto>> reactiveListItemDtoRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        ObjectMapper objectMapper = new ObjectMapper();

        JavaType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, ItemDto.class);

        Jackson2JsonRedisSerializer<List<ItemDto>> serializer = new Jackson2JsonRedisSerializer<>(listType);
        serializer.setObjectMapper(objectMapper);

        RedisSerializationContext<String, List<ItemDto>> context =
                RedisSerializationContext
                        .<String, List<ItemDto>>newSerializationContext(new StringRedisSerializer())
                        .value(serializer)
                        .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

}
