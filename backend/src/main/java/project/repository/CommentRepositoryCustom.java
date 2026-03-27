package project.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;
import project.entity.Comment;
import project.entity.Post;
import project.entity.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CommentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 呼叫 sp_add_comment 儲存程序新增留言
     *
     * @param userId  使用者ID
     * @param postId  發文ID
     * @param content 留言內容
     * @return Comment 新建立的留言物件
     */
    public Comment addComment(Long userId, Long postId, String content) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_add_comment")
                .registerStoredProcedureParameter("p_user_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_post_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_content", String.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_user_id", userId)
                .setParameter("p_post_id", postId)
                .setParameter("p_content", content);

        query.execute();

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        return mapToComment(results.get(0));
    }

    /**
     * 呼叫 sp_get_comments_by_post_id 儲存程序取得留言列表
     *
     * @param postId 發文ID
     * @return List<Comment> 留言列表
     */
    public List<Comment> findByPostId(Long postId) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_get_comments_by_post_id")
                .registerStoredProcedureParameter("p_post_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_post_id", postId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<Comment> comments = new ArrayList<>();
        for (Object[] row : results) {
            comments.add(mapToComment(row));
        }

        return comments;
    }

    /**
     * 呼叫 sp_delete_comment 儲存程序刪除留言
     *
     * @param commentId 留言ID
     * @param userId    使用者ID（用於驗證權限）
     */
    public void deleteComment(Long commentId, Long userId) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_delete_comment")
                .registerStoredProcedureParameter("p_comment_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_user_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_comment_id", commentId)
                .setParameter("p_user_id", userId);

        query.execute();
    }

    /**
     * 將查詢結果轉換為 Comment 物件
     */
    private Comment mapToComment(Object[] row) {
        Comment comment = new Comment();
        comment.setCommentId(((Number) row[0]).longValue());

        Post post = new Post();
        post.setPostId(((Number) row[1]).longValue());
        comment.setPost(post);

        User user = new User();
        user.setUserId(((Number) row[2]).longValue());
        user.setUserName((String) row[3]);
        comment.setUser(user);

        comment.setContent((String) row[4]);

        if (row[5] instanceof java.sql.Timestamp) {
            comment.setCreatedAt(((java.sql.Timestamp) row[5]).toLocalDateTime());
        }

        return comment;
    }
}
