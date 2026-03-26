package project.service.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.Dto.CommentRequest;
import project.Dto.CommentResponse;
import project.entity.Comment;
import project.entity.Post;
import project.entity.User;
import project.repository.CommentRepository;
import project.repository.PostRepository;
import project.repository.UserRepository;
import project.service.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentResponse addComment(Long userId, Long postId, CommentRequest request) {
        log.info("開始處理新增留言請求，使用者 ID: {}, 發文 ID: {}", userId, postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("發文不存在，ID: {}", postId);
                    return new RuntimeException("發文不存在");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("使用者不存在，ID: {}", userId);
                    return new RuntimeException("使用者不存在");
                });

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(request.getContent());

        comment = commentRepository.save(comment);
        log.info("新增留言成功，留言 ID: {}", comment.getCommentId());

        return convertToResponse(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        log.info("開始取得發文的所有留言，發文 ID: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("發文不存在，ID: {}", postId);
                    return new RuntimeException("發文不存在");
                });

        List<Comment> comments = commentRepository.findByPostPostIdOrderByCreatedAtAsc(postId);
        log.info("取得 {} 則留言", comments.size());

        return comments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        log.info("開始處理刪除留言請求，留言 ID: {}, 使用者 ID: {}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("留言不存在，ID: {}", commentId);
                    return new RuntimeException("留言不存在");
                });

        if (!comment.getUser().getUserId().equals(userId)) {
            log.warn("無權限刪除此留言，留言 ID: {}, 使用者 ID: {}", commentId, userId);
            throw new RuntimeException("無權限刪除此留言");
        }

        commentRepository.delete(comment);
        log.info("刪除留言成功，留言 ID: {}", commentId);
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setCommentId(comment.getCommentId());
        response.setPostId(comment.getPost().getPostId());
        response.setUserId(comment.getUser().getUserId());
        response.setUserName(comment.getUser().getUserName());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
