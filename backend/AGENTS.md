# AGENTS.md

本文件為代理（Agent）協作指南，提供編譯、測試、程式碼風格等規範。

---

## 1. Build / Lint / Test 指令

### Build

```bash
# 完整建置（跳過測試）
mvn clean package -DskipTests

# 完整建置（含測試）
mvn clean package

# 啟動應用程式（開發模式）
mvn spring-boot:run

# 編譯原始碼
mvn compile
```

### Test

```bash
# 執行所有單元測試
mvn test

# 執行單一測試類別
mvn -Dtest=UserServiceImplTest test

# 執行單一測試方法
mvn -Dtest=UserServiceImplTest#registUser_Success test

# 執行特定測試類別的所有方法（萬用字元）
mvn -Dtest=*ServiceTest test
```

### Lint / Code Quality

本專案未配置 Checkstyle、PMD 等靜態分析工具。如需新增，可在 `pom.xml` 中加入：

```bash
# 僅編譯並檢查語法
mvn compile

# 格式化（若引入 Spotless）
mvn spotless:apply
```

---

## 2. 專案結構

```
src/
├── main/java/project/
│   ├── BackendApplication.java      # 主程式入口
│   ├── Config/                      # 設定類別（SecurityConfig 等）
│   ├── controller/                  # REST 控制器
│   ├── service/                     # 服務介面
│   │   └── serviceImpl/             # 服務實作類別
│   ├── repository/                  # JPA Repository 介面
│   ├── entity/                      # JPA 實體類別
│   ├── Dto/                         # 資料傳輸物件
│   ├── common/                      # 共用類別（Result 等）
│   └── exception/                   # 例外處理
├── main/resources/
│   └── application.properties       # 應用程式設定
├── DB/
│   └── schema.sql                   # 資料庫 DDL
└── test/java/project/               # 單元測試
```

---

## 3. 技術堆疊

| 元件 | 版本/說明 |
|------|----------|
| Java | 17 |
| Spring Boot | 3.2.4 |
| 資料庫 | MySQL |
| ORM | Spring Data JPA |
| 安全性 | Spring Security + JWT (jjwt 0.12.5) |
| 建置工具 | Maven |
| 測試框架 | JUnit 5 + Spring Boot Test |
| 工具庫 | Lombok |

---

## 4. 程式碼風格指南

### 命名慣例

| 類型 | 命名規則 | 範例 |
|------|---------|------|
| 套件 | 小寫 | `project.controller` |
| 類別 | PascalCase | `UserServiceImpl` |
| 介面 | PascalCase | `UserService` |
| 方法 | camelCase | `registUser()` |
| 變數 | camelCase | `phoneNumber` |
| 常數 | UPPER_SNAKE_CASE | `MAX_SIZE` |
| DTO | PascalCase + Request/Response | `RegisterRequest` |
| Entity | PascalCase（對應資料表） | `User`, `Post` |

### 套件命名備註

本專案套件命名不完全遵循 Java 慣例，部分使用 PascalCase：
- `Config`（應為 `config`）
- `Dto`（應為 `dto`）

**新增程式碼時，請維持現有命名慣例以保持一致性。**

### Imports 規範

```java
// 第一組：Jakarta / Java 標準函式庫
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

// 第二組：第三方函式庫
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.Data;

// 第三組：專案內部
import project.entity.User;
import project.repository.UserRepository;
```

- 避免使用萬用字元匯入（`*`），但 JPA 相關 (`jakarta.persistence.*`) 可接受
- 各組之間以空行分隔

### 類別結構順序

```java
package project.xxx;

// 1. Imports
import ...

// 2. 類別註解
@RestController
@RequestMapping("/api/xxx")

// 3. 類別宣告
public class XxxController {

    // 4. 欄位注入
    @Autowired
    private XxxService xxxService;

    // 5. 靜態方法
    public static ...

    // 6. 建構子
    public XxxController() { }

    // 7. 公開方法（API 端點）
    @GetMapping
    public Result<?> method() { }

    // 8. 私有方法
    private void helper() { }
}
```

### 依賴注入

使用欄位注入（Field Injection）搭配 `@Autowired`：

```java
@Autowired
private UserService userService;
```

### JPA Entity 範例

```java
@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
```

### Service 介面與實作

- 介面放在 `project.service`
- 實作放在 `project.service.serviceImpl`，類別名稱以 `Impl` 結尾
- 使用 `@Service` 註解

```java
// 介面
public interface UserService {
    void registUser(RegisterRequest request);
}

// 實作
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registUser(RegisterRequest request) { ... }
}
```

### API 回應格式

使用 `Result<T>` 統一回應格式：

```java
// 成功
return Result.success("操作成功");
return Result.success(data);

// 失敗
return Result.error(400, "錯誤訊息");
```

### 例外處理

使用 `@RestControllerAdvice` 統一處理例外，並使用 Log 記錄：

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("輸入驗證失敗");
        log.warn("輸入驗證失敗: {}", message);
        return Result.error(400, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.warn("業務邏輯錯誤: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系統發生未知錯誤", e);
        return Result.error(500, "系統發生未知錯誤");
    }
}
```

### Transaction 管理

涉及多表操作時，使用 `@Transactional`：

```java
@Transactional(rollbackFor = Exception.class)
public void createUserWithProfile(RegisterRequest request) { ... }
```

---

## 5. 安全性規範

- 密碼使用 `BCryptPasswordEncoder` 雜湊儲存
- API 端點透過 Spring Security 控制存取權限
- 註冊 API 開放存取（`permitAll`），其餘需驗證
- 使用 JWT 進行身份驗證
- 防止 SQL Injection：使用 JPA Repository，避免原生 SQL
- 防止 XSS：前端輸出編碼，後端驗證輸入

---

## 6. 測試規範

### 開發流程：先寫測試，再實作功能（TDD）

**每次新增功能時，請遵循以下順序：**

1. 撰寫測試（定義預期行為）
2. 執行測試（確認測試失敗）
3. 撰寫實作程式碼
4. 執行測試（確認測試通過）
5. 重構程式碼（如需要）

### 測試類別規範

- 測試類別放在 `src/test/java/project/`
- 測試類別命名：`{類別名稱}Test`
- 使用 JUnit 5 註解：`@Test`
- 測試方法命名：`{方法名稱}_{情境}`，例如 `registUser_Success`
- 遵循 AAA 模式：Arrange（準備）、Act（執行）、Assert（驗證）

### 測試層級與 Mock 策略

| 層級 | 註解 | Mock 物件 | 連接資料庫 |
|------|------|----------|-----------|
| Service | `@ExtendWith(MockitoExtension.class)` | `@Mock` + `@InjectMocks` | ❌ 不連接 |
| Controller | `@WebMvcTest(ClassName.class)` | `@MockBean` | ❌ 不連接 |

**重要：單元測試不應寫入資料庫，使用 Mock 物件模擬依賴。**

### Service 層測試（Mockito）

```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registUser_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0912345678");
        when(userRepository.existsByPhoneNumber("0912345678")).thenReturn(false);

        // Act
        userService.registUser(request);

        // Assert
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registUser_PhoneNumberAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0912345678");
        when(userRepository.existsByPhoneNumber("0912345678")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.registUser(request));
        verify(userRepository, never()).save(any());
    }
}
```

### Controller 層測試（WebMvcTest）

```java
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)  // 禁用 Security 過濾器
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void register_Success() throws Exception {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0912345678");
        doNothing().when(userService).registUser(any());

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(userService).registUser(any());
    }

    @Test
    void register_InvalidInput() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("invalid");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        // 驗證 Service 未被呼叫
        verify(userService, never()).registUser(any());
    }
}
```

### 測試場景覆蓋

每個功能至少測試以下場景：

- **成功路徑**：正常輸入，預期成功
- **驗證失敗**：輸入格式錯誤、必填為空
- **業務邏輯錯誤**：重複資料、權限不足等
- **邊界條件**：最小/最大長度、特殊值

### Log 規範

使用 Lombok `@Slf4j` 加入日誌記錄：

```java
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Override
    public void registUser(RegisterRequest request) {
        log.info("開始處理註冊請求，手機號碼: {}", request.getPhoneNumber());
        // ...
        log.info("使用者註冊成功，手機號碼: {}", user.getPhoneNumber());
    }
}
```

**Log 等級使用規範：**

| 等級 | 使用場景 |
|------|---------|
| ERROR | 系統錯誤、未預期異常 |
| WARN | 業務邏輯警告、驗證失敗 |
| INFO | 重要業務流程（開始/結束） |
| DEBUG | 詳細執行過程（可選） |

---

## 7. Cursor / Copilot 規則

本專案目前無 Cursor 或 Copilot 規則檔案。若未來新增，請放置於：
- Cursor: `.cursor/rules/` 或 `.cursorrules`
- Copilot: `.github/copilot-instructions.md`

---

## 8. 注意事項

- 資料庫 DDL/DML 請存放在 `src/DB/` 目錄下
- 新增資料表時，需同步更新 `schema.sql`
- API 路徑統一使用 `/api/` 前綴
- 跨域設定使用 `@CrossOrigin` 註解
- 實作完與測試完給我commit 內文 但不要自行git commit