# 2theCore
<img width="1080" height="289" alt="image" src="https://github.com/user-attachments/assets/fa54bda9-60be-4c78-a3da-2d852cda9e2f" />


**렌트카·차량 공유 기업 전용 실시간 관제 시스템**

법인 차량, 렌터카, 차량 공유까지
실시간 위치 추적부터 자동 운행 일지까지, 차량 운영의 모든 것을 한 플랫폼에서 해결합니다.

우리 서비스는 렌터카 및 차량 운영 기업이 더 효율적이고 체계적으로 차량을 관리할 수 있도록 설계된 차량 관리·관제 플랫폼입니다.

사용자는 직관적인 인터페이스로 쉽게 사용할 수 있고, 기업은 차량 운영 전반을 데이터 기반으로 체계화할 수 있습니다.

---

🌐 [서비스 링크](http://2thecore-fe.s3-website.ap-northeast-2.amazonaws.com/)

💻 [최종 발표 자료](https://www.canva.com/design/DAGwr9-Q0WI/zMMK7EU8rpots1skrN51MA/view?utm_content=DAGwr9-Q0WI&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=hb8b66b0083)

---

## 주요 기능

### 1. 메인 페이지

- 관리 중인 차량의 현황을 대시보드와 지도를 통해 한 눈에 확인할 수 있습니다.
- 데이터 분석을 통해 차량 수요 트렌드 및 운행 지역 통계 등 다양한 운행 인사이트를 확인할 수 있습니다.

 

### 2. 차량 검색

- 원하는 차량 정보를 특정 항목으로 필터링하여 검색할 수 있습니다.
    - 차량 번호
    - 차량 상태
    - 차량 명

### 3. 차량 등록, 삭제
- 새로운 차량을 등록하고 삭제할 수 있습니다.

### 3. 주행 기록

- 원하는 주행 기록을 특정 항목으로 필터링하여 검색할 수 있습니다.
- 주행 기록을 원하는 Attribute로 정렬하여 볼 수 있습니다.

### 4. 데이터 분석
- 주행 기록을 기반으로 차량 이용 패턴과 브랜드·차급 선호도를 분석해 사용자와 기업 모두에게 유용한 인사이트를 제공합니다.

---

## 시스템 아키텍처

<img width="1481" height="954" alt="image" src="https://github.com/user-attachments/assets/1dd4a264-768b-47af-a69e-34fcc0e85905" />


- **Jenkins를 이용한 자동배포**
    - FE: S3 배포
    - BE: EC2 배포
- **서버 멀티 모듈**
    - Main Server
    - Hub Server
- **RabbitMQ를 활용한 비동기 처리**
- **Redis 캐시 사용**

## 기술 스택
![Java 17](https://img.shields.io/badge/Java%2017-000000?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
![EC2](https://img.shields.io/badge/EC2-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white)
![IAM](https://img.shields.io/badge/AWS%20IAM-DD344C?style=for-the-badge&logo=amazon-aws&logoColor=white)
![RDS](https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazon-aws&logoColor=white)

## BE 팀원 소개
<div align="center">

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/Chyopriushy">
        <img src="https://github.com/Chyopriushy.png" width="100px;" alt="강문경"/>
      </a><br />
      <sub><b>강문경</b></sub>
    </td>
    <td align="center">
      <a href="https://github.com/yeojin-g">
        <img src="https://github.com/yeojin-g.png" width="100px;" alt="구여진"/>
      </a><br />
      <sub><b>구여진</b></sub>
    </td>
    <td align="center">
      <a href="https://github.com/bomi0320">
        <img src="https://github.com/bomi0320.png" width="100px;" alt="권보미"/>
      </a><br />
      <sub><b>권보미</b></sub>
    </td>
    <td align="center">
      <a href="https://github.com/luckyisjelly">
        <img src="https://github.com/luckyisjelly.png" width="100px;" alt="이승진"/>
      </a><br />
      <sub><b>이승진</b></sub>
    </td>
    <td align="center">
      <a href="https://github.com/jongyo0N">
        <img src="https://github.com/jongyo0N.png" width="100px;" alt="이종윤"/>
      </a><br />
      <sub><b>이종윤</b></sub>
    </td>
  </tr>
</table>

</div>

</div>

