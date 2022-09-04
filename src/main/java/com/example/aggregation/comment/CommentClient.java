package com.example.aggregation.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CommentClient {
  private static final String API_ENDPOINT = "https://jsonplaceholder.typicode.com/comments";

  private final WebClient webClient;

  public Flux<Comment> retrieveAllComments() {
    return this.webClient
        .get()
        .uri(API_ENDPOINT)
        .retrieve()
        .bodyToFlux(Comment.class)
        .onErrorResume(throwable -> Mono.empty());
  }

  public Flux<Comment> retrieveCommentsByPostId(final int postId) {
    return this.webClient
        .get()
        .uri(API_ENDPOINT.concat("?postId={postId}"), postId)
        .retrieve()
        .bodyToFlux(Comment.class)
        .onErrorResume(throwable -> Mono.empty());
  }
}
