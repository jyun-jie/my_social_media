package project.service.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.Dto.PostRequest;
import project.Dto.PostResponse;
import project.entity.Post;
import project.repository.PostRepositoryCustom;
import project.repository.UserRepositoryCustom;
import project.service.PostService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepositoryCustom postRepositoryCustom;

    @Autowired
    private UserRepositoryCustom userRepositoryCustom;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostResponse createPost(Long userId, PostRequest request) {
        log.info("開始處理新增發文請求，使用者 ID: {}", userId);

        if (userRepositoryCustom.findById(userId) == null) {
            log.warn("使用者不存在，ID: {}", userId);
            throw new RuntimeException("使用者不存在");
        }

        Post post = postRepositoryCustom.createPost(userId, request.getContent(), request.getImage());
        if (post == null) {
            throw new RuntimeException("發文失敗");
        }
        log.info("發文成功，發文 ID: {}", post.getPostId());

        return convertToResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        log.info("開始取得所有發文");
        List<Post> posts = postRepositoryCustom.findAllPosts();
        log.info("取得 {} 篇發文", posts.size());
        return posts.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostById(Long postId) {
        log.info("開始取得發文，發文 ID: {}", postId);
        Post post = postRepositoryCustom.findById(postId);
        if (post == null) {
            log.warn("發文不存在，ID: {}", postId);
            throw new RuntimeException("發文不存在");
        }
        return convertToResponse(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostResponse updatePost(Long postId, Long userId, PostRequest request) {
        log.info("開始處理編輯發文請求，發文 ID: {}, 使用者 ID: {}", postId, userId);

        Post existingPost = postRepositoryCustom.findById(postId);
        if (existingPost == null) {
            log.warn("發文不存在，ID: {}", postId);
            throw new RuntimeException("發文不存在");
        }

        if (!existingPost.getUser().getUserId().equals(userId)) {
            log.warn("無權限修改此發文，發文 ID: {}, 使用者 ID: {}", postId, userId);
            throw new RuntimeException("無權限修改此發文");
        }

        Post post = postRepositoryCustom.updatePost(postId, userId, request.getContent(), request.getImage());
        log.info("編輯發文成功，發文 ID: {}", postId);

        return convertToResponse(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId, Long userId) {
        log.info("開始處理刪除發文請求，發文 ID: {}, 使用者 ID: {}", postId, userId);

        Post post = postRepositoryCustom.findById(postId);
        if (post == null) {
            log.warn("發文不存在，ID: {}", postId);
            throw new RuntimeException("發文不存在");
        }

        if (!post.getUser().getUserId().equals(userId)) {
            log.warn("無權限刪除此發文，發文 ID: {}, 使用者 ID: {}", postId, userId);
            throw new RuntimeException("無權限刪除此發文");
        }

        postRepositoryCustom.deletePost(postId, userId);
        log.info("刪除發文成功，發文 ID: {}", postId);
    }

    private PostResponse convertToResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setPostId(post.getPostId());
        response.setUserId(post.getUser().getUserId());
        response.setUserName(post.getUser().getUserName());
        response.setContent(post.getContent());
        response.setImage(post.getImage());
        response.setCreatedAt(post.getCreatedAt());
        return response;
    }
}
