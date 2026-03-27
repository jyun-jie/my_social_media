package project.service;

import project.Dto.PostRequest;
import project.Dto.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse createPost(Long userId, PostRequest request);
    List<PostResponse> getAllPosts();
    PostResponse getPostById(Long postId);
    PostResponse updatePost(Long postId, Long userId, PostRequest request);
    void deletePost(Long postId, Long userId);
}
