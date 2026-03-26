package project.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.Dto.PostRequest;
import project.Dto.PostResponse;
import project.common.AuthUtil;
import project.common.Result;
import project.service.PostService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping
    public Result<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        log.info("收到新增發文請求");
        Long userId = authUtil.getCurrentUserId();
        PostResponse response = postService.createPost(userId, request);
        log.info("新增發文流程完成");
        return Result.success(response);
    }

    @GetMapping
    public Result<List<PostResponse>> getAllPosts() {
        log.info("收到取得所有發文請求");
        List<PostResponse> responses = postService.getAllPosts();
        log.info("取得所有發文流程完成");
        return Result.success(responses);
    }

    @GetMapping("/{postId}")
    public Result<PostResponse> getPostById(@PathVariable Long postId) {
        log.info("收到取得發文請求，發文 ID: {}", postId);
        PostResponse response = postService.getPostById(postId);
        log.info("取得發文流程完成");
        return Result.success(response);
    }

    @PutMapping("/{postId}")
    public Result<PostResponse> updatePost(@PathVariable Long postId,
                                           @Valid @RequestBody PostRequest request) {
        log.info("收到編輯發文請求，發文 ID: {}", postId);
        Long userId = authUtil.getCurrentUserId();
        PostResponse response = postService.updatePost(postId, userId, request);
        log.info("編輯發文流程完成");
        return Result.success(response);
    }

    @DeleteMapping("/{postId}")
    public Result<String> deletePost(@PathVariable Long postId) {
        log.info("收到刪除發文請求，發文 ID: {}", postId);
        Long userId = authUtil.getCurrentUserId();
        postService.deletePost(postId, userId);
        log.info("刪除發文流程完成");
        return Result.success("刪除成功");
    }
}
