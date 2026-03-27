package project.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;
import project.entity.User;

import java.util.List;

@Repository
public class UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 呼叫 sp_register_user 儲存程序註冊使用者
     *
     * @param userName     使用者名稱
     * @param phoneNumber  手機號碼
     * @param email        電子郵件
     * @param password     加密後的密碼
     */
    public void registerUser(String userName, String phoneNumber, String email, String password) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_register_user")
                .registerStoredProcedureParameter("p_user_name", String.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_phone_number", String.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_email", String.class, jakarta.persistence.ParameterMode.IN)
                .registerStoredProcedureParameter("p_password", String.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_user_name", userName)
                .setParameter("p_phone_number", phoneNumber)
                .setParameter("p_email", email)
                .setParameter("p_password", password);

        query.execute();
    }

    /**
     * 呼叫 sp_login_user 儲存程序取得使用者資訊
     *
     * @param phoneNumber 手機號碼
     * @return User 使用者物件，如果不存在則回傳 null
     */
    public User findByPhoneNumber(String phoneNumber) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_login_user")
                .registerStoredProcedureParameter("p_phone_number", String.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_phone_number", phoneNumber);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        Object[] row = results.get(0);
        User user = new User();
        user.setUserId(((Number) row[0]).longValue());
        user.setUserName((String) row[1]);
        user.setPhoneNumber((String) row[2]);
        user.setEmail((String) row[3]);
        user.setPassword((String) row[4]);

        return user;
    }

    /**
     * 呼叫 sp_get_user_by_id 儲存程序取得使用者資訊
     * 注意：此方法會取得完整使用者資訊（包含密碼），用於 JWT 驗證
     *
     * @param userId 使用者ID
     * @return User 使用者物件，如果不存在則回傳 null
     */
    public User findById(Long userId) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_get_user_by_id")
                .registerStoredProcedureParameter("p_user_id", Long.class, jakarta.persistence.ParameterMode.IN)
                .setParameter("p_user_id", userId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        Object[] row = results.get(0);
        User user = new User();
        user.setUserId(((Number) row[0]).longValue());
        user.setUserName((String) row[1]);
        user.setPhoneNumber((String) row[2]);
        user.setEmail((String) row[3]);
        user.setCoverImage((String) row[4]);
        user.setBiography((String) row[5]);

        String phoneNumber = user.getPhoneNumber();
        if (phoneNumber != null) {
            User fullUser = findByPhoneNumber(phoneNumber);
            if (fullUser != null) {
                user.setPassword(fullUser.getPassword());
            }
        }

        return user;
    }
}
