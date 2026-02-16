# Module-Tests / 테스트 모듈

## Main Server Tests / 메인 서버 테스트
- Auth/JWT: [back/main-server/src/test/java/com/example/mainserver/JwtTokenProviderTests.java](back/main-server/src/test/java/com/example/mainserver/JwtTokenProviderTests.java#L16), [back/main-server/src/test/java/com/example/mainserver/TokenServiceTest.java](back/main-server/src/test/java/com/example/mainserver/TokenServiceTest.java#L17)
- Admin: [back/main-server/src/test/java/com/example/mainserver/admin/AdminAcceptanceTest.java](back/main-server/src/test/java/com/example/mainserver/admin/AdminAcceptanceTest.java#L25), [back/main-server/src/test/java/com/example/mainserver/admin/controller/AdminApiControllerTest.java](back/main-server/src/test/java/com/example/mainserver/admin/controller/AdminApiControllerTest.java#L31)
- Car: [back/main-server/src/test/java/com/example/mainserver/car/controller/CarControllerTest.java](back/main-server/src/test/java/com/example/mainserver/car/controller/CarControllerTest.java#L40), [back/main-server/src/test/java/com/example/mainserver/car/application/CarServiceTest.java](back/main-server/src/test/java/com/example/mainserver/car/application/CarServiceTest.java#L27)
- Collector: [back/main-server/src/test/java/com/example/mainserver/collector/CollectorControllerTest.java](back/main-server/src/test/java/com/example/mainserver/collector/CollectorControllerTest.java#L35)
- DriveLog: [back/main-server/src/test/java/com/example/mainserver/drivelog/controller/DriveLogControllerTest.java](back/main-server/src/test/java/com/example/mainserver/drivelog/controller/DriveLogControllerTest.java#L30)

## Hub Server Tests / 허브 서버 테스트
- Consumer: [back/hub-server/src/test/java/hub/application/ConsumerServiceTest.java](back/hub-server/src/test/java/hub/application/ConsumerServiceTest.java#L22)
- Repository: [back/hub-server/src/test/java/hub/domain/GpsLogRepositoryTest.java](back/hub-server/src/test/java/hub/domain/GpsLogRepositoryTest.java#L15)
- Integration: [back/hub-server/src/test/java/hub/integration/SumDistCalculationIntegrationTest.java](back/hub-server/src/test/java/hub/integration/SumDistCalculationIntegrationTest.java#L20)

## Known Drift / 알려진 불일치
Some tests reference fields or modules not present in current main code (e.g., `emulatorId` on `CarEntity`, `speed` on `DriveLog`, or emulator readers). Review before relying on test results.
일부 테스트는 현재 코드에 없는 필드/모듈을 참조합니다(예: `CarEntity.emulatorId`, `DriveLog.speed`, emulator reader). 테스트 신뢰 전 확인이 필요합니다.

Related references: [back/common/src/main/java/com/example/common/domain/car/CarEntity.java](back/common/src/main/java/com/example/common/domain/car/CarEntity.java#L16), [back/main-server/src/main/java/com/example/mainserver/drivelog/domain/DriveLog.java](back/main-server/src/main/java/com/example/mainserver/drivelog/domain/DriveLog.java#L17)
