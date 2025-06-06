package com.example.stock1.config;

import com.example.stock1.DTO.StockRateDTO;
import com.example.stock1.entity.ExchangeRateEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Iterator;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() throws Exception {
        System.out.println("🔍 Full VCAP_SERVICES:\n" + System.getenv("VCAP_SERVICES"));
        ObjectMapper mapper = new ObjectMapper();

        String vcap = System.getenv("VCAP_SERVICES");
        if (vcap == null || vcap.isEmpty()) {
            throw new RuntimeException("VCAP_SERVICES environment variable not found.");
        }
        JsonNode root = mapper.readTree(vcap);

        Iterator<String> fieldNames = root.fieldNames();

            System.out.println("🔍 Found service: " + root.fieldNames());

        JsonNode redisService = root.path("redis").get(0);

        JsonNode redisCredentials = root.findValues("credentials").get(1);

        String host = redisCredentials.path("hostname").asText();
        int port = redisCredentials.path("port").asInt();
        String password = redisCredentials.path("password").asText();
        boolean tlsEnabled = redisCredentials.path("tls").asBoolean();

        System.out.println("🔧 Redis host: " + host);
        System.out.println("🔧 Redis port: " + port);
        System.out.println("🔧 Redis password: " + password);
        System.out.println("🔧 TLS enabled: " + tlsEnabled);

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(RedisPassword.of(password));

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl() // ✅ Enforce SSL properly
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    @Qualifier("exchangeRateRedisTemplate")
    public RedisTemplate<String, ExchangeRateEntity> exchangeRateRedisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, ExchangeRateEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // ✅ Configure custom JSON serializer
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
    @Bean
    @Qualifier("stockRateRedisTemplate")
    public RedisTemplate<String, StockRateDTO> stockRateRedisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, StockRateDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 🟢 Correct ObjectMapper setup
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // ✅ Handle LocalDate
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ✅ write as "yyyy-MM-dd"
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // ✅ Redis serializer using correct ObjectMapper
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Set key & value serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // ✅ Configure custom JSON serializer
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
