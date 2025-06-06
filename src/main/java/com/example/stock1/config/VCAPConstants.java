package com.example.stock1.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class VCAPConstants {
    @Value("${vcap.services.postgres.credentials.user:#{null}}")
    private String postgresUser;

    @Value("${vcap.services.postgres.credentials.password:#{null}}")
    private String postgresPassword;

    @Value("${vcap.services.postgres.credentials.url:#{null}}")
    private String postgresURL;

    @Value("${vcap.services.redis.credentials.uri:#{null}}")
    private String redisURI;
}
