# Module-Main-Server-Admin / 메인서버-관리자

## APIs / API
- POST /api/admin/signup [back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java#L21)
- PUT /api/admin/{loginId} [back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java#L32)
- DELETE /api/admin/{loginId} [back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java#L43)

## Key Classes / 주요 클래스
- `AdminApiController`: REST API. [back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/AdminApiController.java#L16)
- `AdminService`: 가입/수정/삭제 로직. [back/main-server/src/main/java/com/example/mainserver/admin/application/AdminService.java](back/main-server/src/main/java/com/example/mainserver/admin/application/AdminService.java#L16)
- `AdminEntity`: 관리자 엔티티. [back/main-server/src/main/java/com/example/mainserver/admin/domain/AdminEntity.java](back/main-server/src/main/java/com/example/mainserver/admin/domain/AdminEntity.java#L21)
- `AdminRepository`: JPA repository. [back/main-server/src/main/java/com/example/mainserver/admin/infrastructure/AdminRepository.java](back/main-server/src/main/java/com/example/mainserver/admin/infrastructure/AdminRepository.java#L10)
- Exceptions: [back/main-server/src/main/java/com/example/mainserver/admin/exception/AdminLoginIdAlreadyExistsException.java](back/main-server/src/main/java/com/example/mainserver/admin/exception/AdminLoginIdAlreadyExistsException.java#L7), handlers [back/main-server/src/main/java/com/example/mainserver/admin/exception/exceptionhandler/AdminExceptionHandler.java](back/main-server/src/main/java/com/example/mainserver/admin/exception/exceptionhandler/AdminExceptionHandler.java#L11)

## Example / 예시
```json
{ "loginId": "admin01", "password": "pw", "name": "관리자", "phoneNumber": "010-0000-0000", "email": "admin@example.com", "birthdate": "2000-01-01" }
```
DTO reference: [back/main-server/src/main/java/com/example/mainserver/admin/controller/dto/AdminRequest.java](back/main-server/src/main/java/com/example/mainserver/admin/controller/dto/AdminRequest.java#L14)
