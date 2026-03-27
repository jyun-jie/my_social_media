package project.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "留言內容不得為空")
    @Size(max = 500, message = "留言內容不得超過 500 字")
    private String content;
}
