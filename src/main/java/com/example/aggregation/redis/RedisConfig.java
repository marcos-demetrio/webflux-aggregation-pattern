package com.example.aggregation.redis;

import io.lettuce.core.api.reactive.RedisReactiveCommands;
import javax.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
  @Bean
  public RedisConnection redisConnection() {
    return new RedisConnection("localhost", "6379");
  }

  @Bean
  public RedisReactiveCommands<String, String> redisReactiveCommands() {
    return redisConnection().getRedisConnection().reactive();
  }

  @PreDestroy
  public void close() {
    redisConnection().stop();
  }
}
