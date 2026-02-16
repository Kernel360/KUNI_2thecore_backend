# Module-Common / 공통 모듈

## Purpose / 목적
Shared entities, DTOs, JWT utilities, and repository interfaces used across main and hub servers.
메인/허브 서버 공용 엔티티, DTO, JWT 유틸, 리포지토리 인터페이스를 제공합니다.

## Key Classes / 주요 클래스
- `JwtTokenProvider`: JWT 생성/검증. [back/common/src/main/java/com/example/common/domain/auth/JwtTokenProvider.java](back/common/src/main/java/com/example/common/domain/auth/JwtTokenProvider.java#L19)
- `CarEntity`: 차량 도메인 엔티티. [back/common/src/main/java/com/example/common/domain/car/CarEntity.java](back/common/src/main/java/com/example/common/domain/car/CarEntity.java#L16)
- `CarReader`: 차량 조회 포트. [back/common/src/main/java/com/example/common/domain/car/CarReader.java](back/common/src/main/java/com/example/common/domain/car/CarReader.java#L10)
- `CarStatus` + converter: 상태 enum 및 변환기. [back/common/src/main/java/com/example/common/domain/car/CarStatus.java](back/common/src/main/java/com/example/common/domain/car/CarStatus.java#L8), [back/common/src/main/java/com/example/common/domain/car/config/CarStatusConverter.java](back/common/src/main/java/com/example/common/domain/car/config/CarStatusConverter.java#L8)
- `ApiResponse`: 표준 응답 래퍼. [back/common/src/main/java/com/example/common/dto/ApiResponse.java](back/common/src/main/java/com/example/common/dto/ApiResponse.java#L10)
- `CarRequestDto`: 차량 등록/수정 요청 DTO. [back/common/src/main/java/com/example/common/dto/CarRequestDto.java](back/common/src/main/java/com/example/common/dto/CarRequestDto.java#L15)
- `CarRepository`: JPA 접근. [back/common/src/main/java/com/example/common/infrastructure/car/CarRepository.java](back/common/src/main/java/com/example/common/infrastructure/car/CarRepository.java#L14)
- `CarReaderImpl`: DB 기반 읽기 구현. [back/common/src/main/java/com/example/common/infrastructure/car/CarReaderImpl.java](back/common/src/main/java/com/example/common/infrastructure/car/CarReaderImpl.java#L19)
- `JwtProperties`: JWT 시크릿 설정 바인딩. [back/common/src/main/java/com/example/common/infrastructure/JwtProperties.java](back/common/src/main/java/com/example/common/infrastructure/JwtProperties.java#L10)
- `PageWrapper`: 페이징 응답 래퍼. [back/common/src/main/java/com/example/common/wrapper/PageWrapper.java](back/common/src/main/java/com/example/common/wrapper/PageWrapper.java#L13)

## Example / 예시
```java
ApiResponse<CarDetailDto> response = ApiResponse.success("ok", dto);
```
Reference: [back/common/src/main/java/com/example/common/dto/ApiResponse.java](back/common/src/main/java/com/example/common/dto/ApiResponse.java#L10)
