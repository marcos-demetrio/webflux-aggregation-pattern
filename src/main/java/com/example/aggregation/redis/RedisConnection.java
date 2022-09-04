package com.example.aggregation.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.api.StatefulRedisConnection;

public class RedisConnection {
  private static final Integer RETRY_COUNT = 5;
  private final String host;
  private final String port;

  RedisClient redisClient;
  StatefulRedisConnection<String, String> connection;

  public RedisConnection(String host, String port) {
    this.host = host;
    this.port = port;
  }

  public void start() {
    if (connection != null && connection.isOpen()) {
      return;
    }

    if (redisClient == null) {
      redisClient = RedisClient.create(createRedisUri());
    }

    if (connection == null || !connection.isOpen()) {
      connection = connect(RETRY_COUNT);
    }

    getRedisConnection().async().clientSetname("SpringRedisExample");
  }

  public void stop() {
    if (connection != null) {
      connection.close();
    }

    if (redisClient != null) {
      redisClient.shutdown();
    }
  }

  public StatefulRedisConnection<String, String> getRedisConnection() {
    if (connection == null || !connection.isOpen()) {
      start();
    }
    return connection;
  }

  private String createRedisUri() {
    return "redis://".concat(host).concat(":").concat(port);
  }

  private StatefulRedisConnection<String, String> connect(final int retryCount) {
    int retriesLeft = retryCount;

    try {
      return redisClient.connect();
    } catch (RedisConnectionException e) {
      if (retriesLeft > 0) {
        return connect(--retriesLeft);
      } else {
        throw new IllegalStateException("Cannot connect to Redis", e);
      }
    }
  }
}
