package com.example.aggregation.postaggregator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("posts")
public class PostAggregateController {

  @Autowired private PostAggregateService service;

  @GetMapping("1")
  public Mono<ResponseEntity<PostAggregate>> findPostsApproach1() {
    return service
        .findPostsApproach1()
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping("2")
  public Mono<ResponseEntity<PostAggregate>> findPostsApproach2() {
    return service
        .findPostsApproach2()
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }
}
