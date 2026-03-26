package project.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId ;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    private String email;
    private String password; // 這裡儲存的是 BCrypt 雜湊值

    @Column(name = "cover_image")
    private String coverImage;
    private String biography;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
