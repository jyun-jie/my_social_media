package project.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;
import project.entity.Post;
import project.entity.User;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 呼叫 sp_create_post 儲存程序新增發文
     *
     * @param userId  使用者ID
     * @param content 發文內容
     * @param image   圖片URL（選填）
     * @return Post 新建立的發文物件
     */
    public Post createPost(Long userId, String content, String image) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_create_post")
                .registerStoredProcedureParameter("p_user_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_content", String.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_image", String.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_user_id", userId)
                .setParameter("p_content", content)
                .setParameter("p_image", image);

        query.execute();

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        return mapToPost(results.get(0));
    }

    /**
     * 呼叫 sp_get_all_posts 儲存程序取得所有發文
     *
     * @return List<Post> 發文列表
     */
    public List<Post> findAllPosts() {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_get_all_posts");

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        List<Post> posts = new ArrayList<>();
        for (Object[] row : results) {
            posts.add(mapToPost(row));
        }

        return posts;
    }

    /**
     * 呼叫 sp_get_post_by_id 儲存程序取得發文
     *
     * @param postId 發文ID
     * @return Post 發文物件，如果不存在則回傳 null
     */
    public Post findById(Long postId) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_get_post_by_id")
                .registerStoredProcedureParameter("p_post_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_post_id", postId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        return mapToPost(results.get(0));
    }

    /**
     * 呼叫 sp_update_post 儲存程序更新發文
     *
     * @param postId  發文ID
     * @param userId  使用者ID（用於驗證權限）
     * @param content 新內容
     * @param image   新圖片URL
     * @return Post 更新後的發文物件
     */
    public Post updatePost(Long postId, Long userId, String content, String image) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_update_post")
                .registerStoredProcedureParameter("p_post_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_user_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_content", String.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_image", String.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_post_id", postId)
                .setParameter("p_user_id", userId)
                .setParameter("p_content", content)
                .setParameter("p_image", image);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        return mapToPost(results.get(0));
    }

    /**
     * 呼叫 sp_delete_post 儲存程序刪除發文
     *
     * @param postId 發文ID
     * @param userId 使用者ID（用於驗證權限）
     */
    public void deletePost(Long postId, Long userId) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_delete_post")
                .registerStoredProcedureParameter("p_post_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_user_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_post_id", postId)
                .setParameter("p_user_id", userId);

        query.execute();
    }

    /**
     * 將查詢結果轉換為 Post 物件
     */
    private Post mapToPost(Object[] row) {
        Post post = new Post();
        post.setPostId(((Number) row[0]).longValue());

        User user = new User();
        user.setUserId(((Number) row[1]).longValue());
        user.setUserName((String) row[2]);
        post.setUser(user);

        post.setContent((String) row[3]);
        post.setImage((String) row[4]);

        if (row[5] instanceof Timestamp) {
            post.setCreatedAt(((Timestamp) row[5]).toLocalDateTime());
        }

        return post;
    }
}
