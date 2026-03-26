package project.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "手機號碼不得為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式錯誤，需為 09 開頭共 10 碼")
    private String phoneNumber;

    @NotBlank(message = "密碼不得為空")
    @Size(min = 8, message = "密碼長度至少 8 位")
    private String password;
}
