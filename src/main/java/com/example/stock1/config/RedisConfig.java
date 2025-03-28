package com.example.stock1.config;

import com.example.stock1.entity.ExchangeRateEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<String, ExchangeRateEntity> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, ExchangeRateEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String for key serializer
        template.setKeySerializer(new StringRedisSerializer());

        // Use JSON for value serializer (ExchangeRateEntity)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }


}
