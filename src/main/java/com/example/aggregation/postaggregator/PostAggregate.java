package com.example.aggregation.postaggregator;

import com.example.aggregation.post.Post;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor(staticName = "create")
public class PostAggregate {
  private List<Post> posts;
}
