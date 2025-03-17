package ua.in.photomap.notificationapi.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.in.photomap.notificationapi.util.MarshalUtils;

@Configuration
@Slf4j
public class RedisConfig {
    private static final String REDIS_SCHEMA_TEMPLATE = "redis://%s:%s";

    @Value(value = "${photomap.redis.host}")
    private String redisHostname;

    @Value(value = "${photomap.redis.port}")
    private int redisPort;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = buildConfig();
        return Redisson.create(config);
    }

    private Config buildConfig() {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format(REDIS_SCHEMA_TEMPLATE, redisHostname, redisPort));
        config.setCodec(new JsonJacksonCodec(MarshalUtils.getMapper()));
        return config;
    }
}
