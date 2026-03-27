package project.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.Dto.CommentRequest;
import project.Dto.CommentResponse;
import project.common.AuthUtil;
import project.common.Result;
import project.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping
    public Result<CommentResponse> addComment(@PathVariable Long postId,
                                              @Valid @RequestBody CommentRequest request) {
        log.info("收到新增留言請求，發文 ID: {}", postId);
        Long userId = authUtil.getCurrentUserId();
        CommentResponse response = commentService.addComment(userId, postId, request);
        log.info("新增留言流程完成");
        return Result.success(response);
    }

    @GetMapping
    public Result<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        log.info("收到取得留言請求，發文 ID: {}", postId);
        List<CommentResponse> responses = commentService.getCommentsByPostId(postId);
        log.info("取得留言流程完成");
        return Result.success(responses);
    }

    @DeleteMapping("/{commentId}")
    public Result<String> deleteComment(@PathVariable Long postId,
                                        @PathVariable Long commentId) {
        log.info("收到刪除留言請求，留言 ID: {}", commentId);
        Long userId = authUtil.getCurrentUserId();
        commentService.deleteComment(commentId, userId);
        log.info("刪除留言流程完成");
        return Result.success("刪除成功");
    }
}
