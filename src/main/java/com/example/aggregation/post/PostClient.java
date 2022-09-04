package com.example.aggregation.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PostClient {
  private static final String API_ENDPOINT = "https://jsonplaceholder.typicode.com/posts";

  private final WebClient webClient;

  public Flux<Post> retrieveAllPosts() {
    return this.webClient
        .get()
        .uri(API_ENDPOINT)
        .retrieve()
        .bodyToFlux(Post.class)
        .onErrorResume(throwable -> Mono.empty());
  }
}
