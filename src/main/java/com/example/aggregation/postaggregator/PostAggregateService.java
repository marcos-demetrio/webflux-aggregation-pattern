package com.example.aggregation.postaggregator;

import static org.springframework.util.ObjectUtils.nullSafeEquals;

import com.example.aggregation.comment.Comment;
import com.example.aggregation.comment.CommentCache;
import com.example.aggregation.comment.CommentClient;
import com.example.aggregation.post.Post;
import com.example.aggregation.post.PostClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Service
@AllArgsConstructor
public class PostAggregateService {
  private final PostClient postClient;
  private final CommentClient commentClient;
  private final CommentCache commentCache;

  public Mono<PostAggregate> findPostsApproach1() {
    return Mono.zip(
            postClient.retrieveAllPosts().collectList(),
            commentClient.retrieveAllComments().collectList())
        .map(
            objects -> {
              var posts = objects.getT1();
              var comments = objects.getT2();

              return posts.stream()
                  .peek(post -> post.setComments(filterPostComments(comments, post.getId())))
                  .collect(Collectors.toList());
            })
        .map(this::combine);
  }

  // In this approach the process is slow due to many requests, so we use cache
  public Mono<PostAggregate> findPostsApproach2() {
    return postClient
        .retrieveAllPosts()
        .flatMap(
            post ->
                getCommentsByPostId(post.getId())
                    .collectList()
                    .map(comments -> Tuples.of(post, comments)))
        .map(
            objects -> {
              var post = objects.getT1();
              var comments = objects.getT2();

              post.setComments(comments);

              return post;
            })
        .collectList()
        .map(this::combine);
  }

  private Flux<Comment> getCommentsByPostId(final int postId) {
    var commentFlux =
        commentClient
            .retrieveCommentsByPostId(postId)
            .collectList()
            .flatMapMany(comments -> commentCache.insert(postId, comments));

    return commentCache.get(postId).switchIfEmpty(commentFlux);
  }

  private List<Comment> filterPostComments(List<Comment> comments, int postId) {
    return comments.stream()
        .filter(comment -> nullSafeEquals(postId, comment.getPostId()))
        .collect(Collectors.toList());
  }

  private PostAggregate combine(List<Post> posts) {
    return PostAggregate.create(posts);
  }
}
