package com.example.aggregation.comment;

import static io.lettuce.core.SetArgs.Builder.ex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class CommentCache {
  private static final String KEY_PREFIX_COMMENT = "Aggregate:comment-postId:%s";
  private static final long TTL_COMMENT = 60L;

  private final RedisReactiveCommands<String, String> reactiveCommands;
  private final ObjectMapper mapper;

  public Flux<Comment> insert(final int postId, List<Comment> comments) {
    var key = String.format(KEY_PREFIX_COMMENT, postId);

    return Mono.fromCallable(() -> mapper.writeValueAsString(comments))
        .onErrorResume(
            t -> Mono.error(new IllegalArgumentException("Unable to convert using ObjectMapper")))
        .flatMap(value -> reactiveCommands.set(key, value, ex(TTL_COMMENT)).thenReturn(comments))
        .flatMapMany(Flux::fromIterable);
  }

  public Flux<Comment> get(final int postId) {
    var key = String.format(KEY_PREFIX_COMMENT, postId);

    return reactiveCommands
        .get(key)
        .map(
            value -> {
              try {
                return mapper.readValue(value, new TypeReference<List<Comment>>() {});
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            })
        .flatMapMany(Flux::fromIterable);
  }
}
