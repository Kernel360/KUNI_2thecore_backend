# Module-Main-Server-Auth / 메인서버-인증

## APIs / API
- POST /api/auth/login [back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java](back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java#L26)
- POST /api/auth/logout [back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java](back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java#L32)
- POST /api/auth/verify [back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java](back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java#L59)

## Key Classes / 주요 클래스
- `AuthController`: 인증 API. [back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java](back/main-server/src/main/java/com/example/mainserver/auth/controller/AuthController.java#L20)
- `AuthService`: 로그인/자동로그인 처리. [back/main-server/src/main/java/com/example/mainserver/auth/application/AuthService.java](back/main-server/src/main/java/com/example/mainserver/auth/application/AuthService.java#L30)
- `TokenService`: Redis 토큰 관리. [back/main-server/src/main/java/com/example/mainserver/auth/application/TokenService.java](back/main-server/src/main/java/com/example/mainserver/auth/application/TokenService.java#L16)
- `JwtAuthenticationFilter`: JWT 필터. [back/main-server/src/main/java/com/example/mainserver/auth/infrastructure/JwtAuthenticationFilter.java](back/main-server/src/main/java/com/example/mainserver/auth/infrastructure/JwtAuthenticationFilter.java#L32)
- `SecurityConfig`: 보안 설정/화이트리스트. [back/main-server/src/main/java/com/example/mainserver/auth/infrastructure/SecurityConfig.java](back/main-server/src/main/java/com/example/mainserver/auth/infrastructure/SecurityConfig.java#L28)
- `JwtTokenProvider`: 토큰 생성/검증. [back/common/src/main/java/com/example/common/domain/auth/JwtTokenProvider.java](back/common/src/main/java/com/example/common/domain/auth/JwtTokenProvider.java#L19)

## DTOs / DTO
- `LoginRequest`, `RefreshRequest`, `TokenDto`: [back/main-server/src/main/java/com/example/mainserver/auth/domain/LoginRequest.java](back/main-server/src/main/java/com/example/mainserver/auth/domain/LoginRequest.java#L11), [back/main-server/src/main/java/com/example/mainserver/auth/domain/RefreshRequest.java](back/main-server/src/main/java/com/example/mainserver/auth/domain/RefreshRequest.java#L10), [back/main-server/src/main/java/com/example/mainserver/auth/domain/TokenDto.java](back/main-server/src/main/java/com/example/mainserver/auth/domain/TokenDto.java#L13)

## Example / 예시
```json
{ "loginId": "admin01", "password": "pw" }
```
DTO reference: [back/main-server/src/main/java/com/example/mainserver/auth/domain/LoginRequest.java](back/main-server/src/main/java/com/example/mainserver/auth/domain/LoginRequest.java#L11)
