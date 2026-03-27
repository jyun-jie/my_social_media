package project.service;

import project.Dto.CommentRequest;
import project.Dto.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(Long userId, Long postId, CommentRequest request);
    List<CommentResponse> getCommentsByPostId(Long postId);
    void deleteComment(Long commentId, Long userId);
}
