package project.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;



    // 成功時的靜態方法 (有回傳資料)
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    // 成功時的靜態方法 (純文字提示，無資料)
    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    // 失敗時的靜態方法
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}
