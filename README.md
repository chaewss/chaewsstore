# 🏷 chaewsstore
![Coverage](https://sonarcloud.io/api/project_badges/measure?project=chaewss_chaewsstore&metric=coverage) ![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=chaewss_chaewsstore&metric=reliability_rating) ![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=chaewss_chaewsstore&metric=security_rating) ![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=chaewss_chaewsstore&metric=sqale_rating) ![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=chaewss_chaewsstore&metric=vulnerabilities) ![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=chaewss_chaewsstore&metric=code_smells)  
<br>
다양한 상품을 판매하고 구매할 수 있는 거래 서비스입니다.

<br>     

## ⚒️ 사용 기술 및 환경
- Java 17, Spring Boot 3.0.4, Gradle, Spring Data JPA, H2
- Spring Security, JWT, QueryDSL
- Spring REST Docs, GitHub Actions, JaCoCo, SonarCloud, JMeter

<br>  

## ☁️ 프로젝트 주요 관심사

- RESTful API 원칙 준수
- 단일 책임 원칙을 준수하고 불필요한 의존성을 최소화한 멀티 모듈 구성
- Spring Security와 JWT를 이용한 인증 및 인가
- Fetch Join을 사용한 N+1 문제 해결
- 경매 시스템 동시성 문제 해결
- 개별 기능을 독립적으로 검증하기 위한 단위 테스트, 멀티 스레드 환경에서 동시성 문제를 검증하기 위한 통합 테스트 및 JMeter 작성
- Spring REST Docs를 통한 API 문서화
- GitHub Actions를 사용하여 자동화된 빌드, JaCoCo 테스트 커버리지 측정, SonarCloud 정적 분석 파이프라인 구축

<br>  

## ☄️ 트러블 슈팅
* 스파게티 소스가 되지 않기 위한 멀티 모듈 재구성 - SRP를 적용한 모듈 분리

  [**내가 적용한!!! 멀티 모듈 기준**](https://chaewsscode.tistory.com/241)  

<br>

* JPA에서 N+1 문제 해결하기: 로그를 통한 문제 원인 파악 및 Fetch Join 활용 방법

  [**N+1 문제 발생 지점 찾아 해결하기**](https://chaewsscode.tistory.com/259)

<br>

* 판매 입찰과 구매 입찰의 동시성 문제 해결 - DB Lock

  [**입찰 동시성 문제 해결하기**](https://chaewsscode.tistory.com/242)

<br>

* 멀티스레드 환경에서의 테스트 데이터 초기화 문제와 해결 방안 - @Transactional 사용 시 발생하는 이슈와 JUnit5 Extension 활용

  [**멀티스레드 테스트에서 @Transactional 사용 시 실패하는 이유**](https://chaewsscode.tistory.com/243)  

<br>

* Spring Security 구성과 예외 처리 최적화
  
  [**Spring Security에서의 JWT 예외 처리 - AuthenticationException과 JWTException 분리**](https://chaewsscode.tistory.com/235)

  [**Spring Security permitAll() 적용 안 되는 이유**](https://chaewsscode.tistory.com/233)
  
  [**Spring Security 필터링 효율화 - RequestMatcher로 중복 제거하기**](https://chaewsscode.tistory.com/234)

<br>

* Spring 테스트 환경 최적화 및 문제 해결

  [**테스트 코드에 Test Fixture 사용하기: 테스트 데이터 재사용**](https://chaewsscode.tistory.com/253)
  
  [**로그인 서비스 테스트 AuthenticationManagerBuilder NPE**](https://chaewsscode.tistory.com/236)

  [**@WebMvcTest를 사용한 컨트롤러 테스트 시, JPA 관련 빈들이 로딩되지 않는 문제**](https://chaewsscode.tistory.com/248)  

  [**@DataJpaTest를 사용한 테스트 실행 시 발생하는 IllegalStateException과 NoSuchBeanDefinitionException 등의 오류 해결**](https://chaewsscode.tistory.com/251)

<br>  

## 📁 프로젝트 구조
```bash
├── chaewsstore-admin  
│       └── com.chaewsstore
│           └── apis # 각 usecase 별 패키지 ex) auth, bid, user
│               └── controller
│               └── dto
│               └── helper  # 해당 모듈이나 기능과 관련된 다양한 보조 작업 수행
│               └── usecase  # 비즈니스 로직 구현, 특정 기능이나 시나리오에 대한 작업 수행
│           ├── common  # 프로젝트에서 공통적으로 사용되는 유틸리티, 예외 처리
│           └── config  # 프로젝트 각종 설정
├── chaewsstore-app
│       └── com.chaewsstore # 어드민과 동일
├── chaewsstore-core
│       └── com.chaewsstore.core
│           ├── domain  # 각 도메인별 엔티티, 레포지토리, 서비스, dto, 에러 코드 등
│           └── infra  # 설정 정보를 동적으로 로드하고 처리하는 기능 제공
└── global-utils  # 프로젝트 전체에서 공통으로 사용되는 유틸리티 기능 제공
```

<br>  

## 📑 API 문서

- [**app 서버**](https://documenter.getpostman.com/view/26301389/2sA3kPo3oE#fbcab513-adb3-45a6-afd8-b9514508c490)

- [**admin 서버**](https://documenter.getpostman.com/view/26301389/2sAXjM5CGb)

<br>     

## 📝 Git 브랜치 전략

Git-Flow 브랜치 전략을 사용하여 관리합니다.
모든 feature 브랜치는 develop 브랜치에서 시작되며 Approve 받은 PR만 develop 브랜치로 merge 됩니다.

- main: 배포 시 사용됩니다.
- develop: PR을 거친 후 승인된 feature 브랜치가 모입니다.
- feature: 기능 개발을 진행할 때 사용됩니다.
- hot-fix: 배포를 진행한 후 발생한 버그를 수정해야 할 때 사용합니다.

<br>     

## 🧪 테스트
- Unit Test는 Mockito를 사용하여 작성되었으며, 테스트 커버리지 80% 이상을 유지하고 있습니다.
- JMeter를 사용한 동시성 테스트 진행  
  - [**Jmeter를 사용한 동시성 테스트**](https://chaewsscode.tistory.com/247)  

<br>  

## 🚀 CI/CD 파이프라인
### GitHub Actions + JaCoCo + SonarCloud
- **GitHub Actions**를 사용하여 자동화된 CI/CD 파이프라인을 구축하였습니다.
- **JaCoCo**를 도입하여 코드의 테스트 커버리지를 측정하고 있습니다.
- 코드 변경 시 JaCoCo가 테스트 커버리지를 측정하며, 80% 이상의 커버리지를 유지하는 것을 목표로 하고 있습니다.
  - [**JaCoCo + JaCoCo Report Aggregation 적용하기**](https://chaewsscode.tistory.com/249)  
  - [**멀티모듈 JaCoCo 집계된 커버리지 보고서를 기준으로 빌드 검증**](https://chaewsscode.tistory.com/252)
- **SonarCloud**를 사용해 코드 커버리지, 코드 스멜, 보안 취약점 등을 검사하고 있습니다.
  - [**정적 코드 분석을 위해 SonarCloud 사용하기**](https://chaewsscode.tistory.com/256)  
- PR 또는 커밋이 develop 브랜치에 푸시될 때마다 테스트 커버리지 측정과 정적 분석이 자동으로 실행되며, 이를 통해 코드 품질을 지속적으로 유지하고 있습니다.
- 코드 커버리지가 일정 기준에 미치지 못하거나, 코드 스멜 및 보안 취약점이 발견되면 merge가 제한되도록 설정하여 유지 보수성과 안정성을 강화하였습니다.

<br>  

## ☁️ DB
![chaewsstore DB](https://github.com/user-attachments/assets/70b45b4f-9941-419d-b1da-fb293307ab7e)

<br>     

