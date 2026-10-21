# EvolveSoft 표준 프레임워크 BE #

## 개요
**Evolve 시스템의 백엔드 API 서버**로,  
**프론트엔드 애플리케이션과 통신하기 위한 RESTful API**를 제공합니다.

`Spring Boot 3.4`, `PostgreSQL`, `Redis`, `Flyway` 기반으로 구축되었으며,  
인증, 권한, 사용자, 게시판, 코드, 로그 등 주요 기능을 API 형태로 제공합니다.

`Swagger(OpenAPI)`를 통해 프론트엔드와의 연동 API를 문서화하고,  
`Docker Compose`를 통해 데이터베이스 및 캐시 환경을 실행할 수 있습니다.

---

### 기술 스택
| 구분                   | 기술                          |
|----------------------|-----------------------------|
| **Language**         | Java 17                     |
| **Framework**        | Spring Boot 3.4.0           |
| **Build Tool**       | Gradle                      |
| **Database**         | PostgreSQL                  |
| **ORM**              | Spring Data JPA             |
| **DB Migration**     | Flyway                      |
| **Cache**            | Redis                       |
| **API Docs**         | Swagger (SpringDoc OpenAPI) |
| **Containerization** | Docker / Docker Compose     |

---

### ERD
https://www.erdcloud.com/d/CtrFC4r7zHwBtckWk

---

### 프로젝트 구조
```text
src
└── main
    ├── java
    │   └── com.evolve.evolvebe
    │       ├── auth/           # JWT 인증 및 로그인/로그아웃
    │       ├── board/          # 게시판
    │       ├── code/           # 공통 코드/코드 그룹 관리
    │       ├── global/         # (exception,log,security,config,util,entity 등 공통)
    │       ├── log/            # 접근 로그/ 로그인 로그
    │       ├── member/         # 사용자 관리
    │       ├── menu/           # 메뉴 관리
    │       ├── message/        # 메시지 관리
    │       ├── role/           # 권한(역할) 관리(메뉴, 사용자, 권한)
    │       └── terms/          # 약관 관리
    │
    │       # 기능별(도메인) 기본 구조 (auth,board,code ... 등)
    │       ├── application/   # 파사드(Facade) 계층
    │       ├── domain/        # 엔티티, 서비스, 리포지토리 등 핵심 비즈니스 로직
    │       ├── infra/         # 외부 연동 구현체 (JPA, Redis, 외부 API 등)
    │       └── interfaces/    # Controller, DTO, Mapper, Request/Response 정의
    │
    └── resources
        ├── config/            # 설정 값 (dev, local, etc.)
        ├── db.migration/      # db init.sql 파일
        ├── static.docs/       # Swagger API 문서 (openapi3.yml)
        └── application.yml    
```

### 아키텍처 개요
```text
┌───────────────────────────────────────────┐
│                  Controller               │  ← interfaces 계층
└──────────────▲────────────────────────────┘
               │
┌──────────────▼────────────────────────────┐
│                  Facade                   │  ← application 계층
└──────────────▲────────────────────────────┘
               │
┌──────────────▼────────────────────────────┐
│           Domain Service / Entity         │  ← domain 계층
└──────────────▲────────────────────────────┘
               │
┌──────────────▼────────────────────────────┐
│           JPA / Redis / API Client        │  ← infra 계층
└───────────────────────────────────────────┘
```
**도메인 중심 설계(DDD, Domain-Driven Design)** 기반으로 구성  
각 도메인은 독립적인 비즈니스 단위를 가지며,
`application`, `domain`, `infra`, `interfaces` 4계층 구조로 구분

- **interfaces (Controller, DTO, Mapper)**  
  클라이언트 요청을 수신하고, 외부로부터 들어온 데이터를 내부 도메인 모델에 맞게 변환  
  → REST API, Request/Response 정의, Swagger 문서화 담당

- **application (Facade)**  
  도메인 로직을 조합하여 하나의 유스케이스(Use Case)를 처리  
  → 서비스 간 조합, 트랜잭션 단위 제어, 응용 로직 처리

- **domain (Entity, Repository, Service)**  
  핵심 비즈니스 규칙과 데이터 구조를 정의하는 핵심 영역  
  → 순수한 비즈니스 로직, JPA Entity, Domain Service, Repository Interface 포함

- **infra (Persistence, External API, Redis 등)**  
  외부 기술 의존성을 처리하는 계층
  → JPA 구현체, Redis, 외부 API 연동, 이메일 발송, 파일 업로드 등

---

### 실행 방법
###  1. Docker Compose 실행
PostgreSQL, Redis, Swagger API 도커 구성
```text
 docker-compose up -d
```

### 2. Gradle 빌드 및 테스트
테스트를 제외한 빠른 빌드는 ./gradlew clean build -x test 사용
```text
 ./gradlew clean build
 ./gradlew test
```
의존성 트리 확인(문제 발생시 분석 용도)
```text
./gradlew dependencies
```

### 3. Spring Boot 실행
```text
 ./gradlew bootRun --args='--spring.profiles.active=local'
```
또는 빌드된 JAR 파일 직접 실행:
```text
 java -jar build/libs/evolve-be-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

### 4. API 문서 확인
http://localhost:8081

---

### JWT Secret Key 규칙
32bytes(256bit) 이상 랜덤 문자열 사용  
대/소문자 + 숫자 + 특수문자 혼합  
Git 저장소 등 소스 코드 내 직접 노출 금지  
로컬/개발 환경은 환경 변수 기반으로 설정  
(예시값: Q2hhbmdlbWVTZWN1cml0S2V5LTMyeUJ5dGVzLVMxIQ==)

### JWT Secret Key 랜덤 생성 방법
### 1) OpenSSL로 32바이트 랜덤 키 생성 (권장)

```text
bash 

openssl rand -base64 32
openssl rand -base64 64
```


### 환경 변수 설정 오류 발생 시 (JWT_SECRET_KEY)
### 1. 프로젝트 루트에 .env 파일 생성
```text
JWT_SECRET_KEY={user-jwt-secret-key}
```
### 2. 터미널에 직접 환경 변수 주입  
```text
bash

export JWT_SECRET_KEY=local-test-secret
```
```text
powershell

$env:JWT_SECRET_KEY="local-test-secret"
```

### 3. IntelliJ IDE 실행 시 환경 변수 설정
Run → Edit Configurations… 메뉴에서  
실행 설정(Spring Boot 또는 Gradle bootRun) 선택 후 Environment variables 입력
```text
JWT_SECRET_KEY=user-jwt-secret-key;
```
---

### 초기 환경 구축 가이드 (IntelliJ IDE)
### 1. 사전 준비
로컬 개발을 시작하기 전에 다음 환경이 설치되어 있어야 합니다.

| 항목                      | 버전 권장                                              | 비고                                |
|-------------------------|----------------------------------------------------|-----------------------------------|
| JDK                     | 17 이상                                              | (OpenJDK 권장)                      |
| Gradle                  | Wrapper(내장 사용)                                     | ./gradlew 사용                      |
| Docker / Docker Compose | 최신 버전                                              | PostgreSQL, Redis 등 컨테이너 실행용      |
| IDE                     | IntelliJ IDEA Ultimate<br/>IntelliJ IDEA Community | Spring Boot 개발에 최적화(유료)<br/>무료 버전 |
| Git                     | 2.x 이상                                             | 버전 관리용                            |

### 2. IntelliJ IDEA Ultimate 설정 방법 (권장)
① 프로젝트 열기
- File → Open → evolve-be 디렉토리 선택
- Gradle 프로젝트 자동 인식됨

② JDK 설정
- File → Project Structure → Project
  - SDK: Java 17
  - Language level: 17 (Sealed types, switch patterns)

③ Spring Boot 설정
- Run → Edit Configurations
- '+' → Spring Boot 선택
- 설정 항목 입력:

| Name                  | EvolveBE                                |
|-----------------------|-----------------------------------------|
| Main class            | com.evolve.evolvebe.EvolveBeApplication |
| Active Profiles       | local                                   |
| VM Options            | -Dspring.profiles.active=local          |
| Working directory     | ${PROJECT_DIR}                          |
| Environment variables | JWT_SECRET_KEY=your-jwt-secret-key      |

- 적용 후 ▶ Run 버튼 실행

### 3. IntelliJ IDEA Community 설정 방법 (무료 버전)
Community 버전은 Spring Boot 자동 인식 기능이 없기 때문에  
일반 Application Run Configuration 으로 수동 실행이 필요

① 프로젝트 열기
- File → Open → evolve-be 디렉토리 선택
- “Import as Gradle Project” 선택

② JDK 설정
- File → Project Structure → Project
  - SDK: Java 17
  - Language level: 17

③ Gradle 설정 확인
- 우측 패널의 Gradle 탭 → Tasks → build → build 더블클릭 → 빌드가 정상 완료되면 환경 설정 완료

④ 실행 설정 (수동)
- Run → Edit Configurations → + → Application 선택
- 아래 항목 설정:

| 항목                      | 값                                       |
|-------------------------|-----------------------------------------|
| Name                    | EvolveBE                                |
| Main class              | com.evolve.evolvebe.EvolveBeApplication |
| Use classpath of module | evolve-be.main                          |
| Program arguments       | --spring.profiles.active=local          |
| Working directory       | ${PROJECT_DIR}                          |
| Environment variables   | JWT_SECRET_KEY=your-jwt-secret-key      |

- 저장 후 ▶ Run 실행

### IntelliJ 환경 팁
| 항목	                             | 설명                                                                                     |
|---------------------------------|----------------------------------------------------------------------------------------|
| Gradle Build vs IntelliJ Build	 | Gradle 빌드(Build → Build Project)를 권장                                                   |
| Annotation Processor            | Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable 체크 |
| Lombok 사용 시                     | Settings → Plugins → Lombok 설치 후 재시작                                                   |
| .env 환경 변수 적용                   | Run Configuration → Environment Variables에 직접 추가 (JWT_SECRET_KEY, DB_URL 등)            |
| Redis / PostgreSQL 연동 확인        | docker ps 명령으로 컨테이너 실행 상태 확인                                                           |

---

### 로컬 환경 실행 요약
1. Docker로 PostgreSQL / Redis / swagger 실행
2. IntelliJ에서 JDK 17 설정 확인
3. Gradle 빌드 (./gradlew clean build)
4. Spring Boot 실행 (Ultimate: Spring Boot Run / Community: Application Run)
5. Swaggger 확인  
http://localhost:8081

---

### 커밋 메시지 기본 룰 (Git)
| 유형       | 설명            |
|----------|---------------|
| feat     | 새로운 기능 추가     |
| fix      | 버그 수정         |
| refactor | 코드 리팩토링       |
| docs     | 문서 수정         |
| style    | 코드 스타일 변경     |
| test     | 테스트 코드 추가     |
| chore    | 빌드 및 환경 설정 변경 |


```text
예시:
feat: 로그인 토큰 자동 갱신 기능 추가
fix: 게시글 페이징 오류 수정
```

---