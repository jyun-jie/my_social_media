package project.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private Long postId;
    private Long userId;
    private String userName;
    private String content;
    private String image;
    private LocalDateTime createdAt;
}
