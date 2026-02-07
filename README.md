# Toy Commerce

Spring Boot(Backend) + React(Frontend) 기반의 토이 커머스 프로젝트입니다.

## 1. 프로젝트 구조

```text
toy-commerce/
├─ src/
│  ├─ main/
│  │  ├─ java/me/gogradually/toycommerce/
│  │  │  ├─ domain/            # 도메인 모델, 도메인 규칙, 도메인 예외
│  │  │  ├─ application/       # 유스케이스 오케스트레이션 서비스
│  │  │  ├─ interfaces/        # Web Controller, DTO, 예외 핸들러
│  │  │  ├─ infrastructure/    # JPA/Redis 구현체, 설정, 외부 어댑터
│  │  │  └─ common/            # 공통 예외/코드
│  │  └─ resources/
│  │     └─ application.yml    # 로컬 실행 기본 설정(H2, Redis, SpringDoc)
│  └─ test/                    # 백엔드 테스트(JUnit5, Spring Test)
├─ frontend/
│  ├─ src/
│  │  ├─ app/                  # 앱 엔트리, 라우터, 레이아웃
│  │  ├─ pages/                # 페이지 컴포넌트
│  │  ├─ features/             # 기능 단위 UI/모델/훅
│  │  ├─ shared/               # 공용 API 클라이언트, UI, 설정
│  │  └─ test/                 # 프런트 테스트(vitest)
│  ├─ scripts/generate-api.mjs # OpenAPI 기반 클라이언트 생성 스크립트
│  └─ package.json
├─ docs/                       # 요구사항/체크리스트/API 문서
├─ build.gradle
└─ settings.gradle
```

## 2. 기술 스택

- Backend: Java 21, Spring Boot, Spring MVC, Spring Data JPA, Spring Data Redis, H2
- Frontend: React 18, TypeScript, Vite, React Router, TanStack Query, Tailwind CSS

## 3. 실행 전 준비

### 필수 도구

- JDK 21
- Node.js 18+ (권장: 20+)
- npm
- Redis

### Redis 빠른 실행 예시 (Docker)

```bash
docker run -d --name toy-commerce-redis -p 6379:6379 redis:7-alpine
```

## 4. 백엔드 실행 방법

백엔드는 `application.yml`에서 Redis 환경변수를 사용하므로, 실행 전에 값을 지정해야 합니다.

```bash
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
./gradlew bootRun
```

기본 접속 정보:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:mem:toy-commerce;MODE=MySQL;DB_CLOSE_DELAY=-1`
    - User: `sa`
    - Password: (빈 값)

## 5. 프런트엔드 실행 방법

새 터미널에서 아래 명령을 실행합니다.

```bash
cd frontend
npm install
npm run dev
```

기본 접속 정보:

- Frontend: `http://localhost:5173`
- API Base URL 기본값: `http://localhost:8080` (`VITE_API_BASE_URL` 미지정 시)

백엔드 주소를 변경해야 하면:

```bash
cd frontend
VITE_API_BASE_URL=http://localhost:8080 npm run dev
```

## 6. 로컬 실행 순서(권장)

1. Redis 실행
2. 루트에서 백엔드 실행: `./gradlew bootRun`
3. `frontend` 디렉터리에서 프런트 실행: `npm run dev`
4. 브라우저에서 `http://localhost:5173` 접속

## 7. 테스트/빌드 명령

### Backend

```bash
./gradlew test
./gradlew clean build
./gradlew bootJar
```

### Frontend

```bash
cd frontend
npm run test
npm run build
npm run lint
```

## 8. OpenAPI 클라이언트 재생성 (Frontend)

백엔드가 실행 중일 때:

```bash
cd frontend
OPENAPI_SOURCE=http://localhost:8080/v3/api-docs npm run generate:api
```

생성 위치: `frontend/src/shared/api/generated/client`
