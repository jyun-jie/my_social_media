package project.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {
    @NotBlank(message = "發文內容不得為空")
    @Size(max = 1000, message = "發文內容不得超過 1000 字")
    private String content;

    private String image;
}
