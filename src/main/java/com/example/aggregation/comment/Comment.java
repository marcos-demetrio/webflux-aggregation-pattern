package com.example.aggregation.comment;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Comment implements Serializable {
  private int id;
  private int postId;
  private String name;
  private String email;
  private String body;
}
