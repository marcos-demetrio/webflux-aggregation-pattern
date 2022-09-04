package com.example.aggregation.post;

import com.example.aggregation.comment.Comment;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Post implements Serializable {
  private int id;
  private String title;
  private String body;
  private List<Comment> comments;
}
