package project.Dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "手機號碼不得為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式錯誤，需為 09 開頭共 10 碼")
    private String phoneNumber;

    @NotBlank(message = "密碼不得為空")
    @Size(min = 8, message = "密碼長度至少 8 位")
    private String password;

    @NotBlank(message = "使用者名稱不得為空")
    @Size(max = 50, message = "使用者名稱長度不得超過 50 字元")
    private String userName;

    @Email(message = "Email 格式錯誤")
    private String email;
}
