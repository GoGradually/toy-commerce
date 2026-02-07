# API 명세 초안 (Agent A, B)

## OpenAPI / Swagger

- OpenAPI JSON: `GET /v3/api-docs`
- Swagger UI: `GET /swagger-ui.html` (또는 `/swagger-ui/index.html`)
- 정적 스펙 파일: `docs/001 프로젝트 요구사항 설정/openapi/agent-a-product-admin-openapi.yaml`

## 공통 응답 포맷

### 성공

```json
{
  "success": true,
  "data": {}
}
```

### 실패

```json
{
  "success": false,
  "error": {
    "code": "PRODUCT-404",
    "message": "상품을 찾을 수 없습니다."
  }
}
```

## 공개 API

### 1) 상품 목록 조회

- `GET /api/products`
- Query
    - `page` (default: `0`)
    - `size` (default: `20`, max: `100`)
    - `sortBy` (`id`, `name`, `price`, `createdAt`)
    - `direction` (`asc`, `desc`)
- 설명: 활성(`ACTIVE`) 상품만 조회

### 2) 상품 상세 조회

- `GET /api/products/{productId}`
- 설명: 활성(`ACTIVE`) 상품만 조회

## 관리자 API

### 3) 상품 생성

- `POST /api/admin/products`
- Request

```json
{
  "name": "레고 스타터 세트",
  "price": 15900,
  "stock": 50,
  "status": "ACTIVE"
}
```

### 4) 상품 수정

- `PATCH /api/admin/products/{productId}`
- Request

```json
{
  "name": "레고 프로 세트",
  "price": 25900,
  "status": "INACTIVE"
}
```

### 5) 상품 재고 수정

- `PATCH /api/admin/products/{productId}/stock`
- Request

```json
{
  "stock": 12
}
```

### 6) 상품 삭제

- `DELETE /api/admin/products/{productId}`

## 상태값 규칙

- `ACTIVE`: 공개 API 노출 대상
- `INACTIVE`: 공개 API 비노출, 관리자 API에서는 조회/수정/삭제 가능

## 찜 API (Agent B)

### 7) 상품 찜 추가

- `POST /api/products/{productId}/wishlist`
- Header
  - `X-Member-Id` (필수, 1 이상)
- 설명
  - 활성(`ACTIVE`) 상품만 찜 가능
  - 동일 회원이 동일 상품을 다시 찜해도 성공 응답(멱등)

### 8) 상품 찜 해제

- `DELETE /api/products/{productId}/wishlist`
- Header
  - `X-Member-Id` (필수, 1 이상)
- 설명
  - 찜이 이미 없어도 성공 응답(멱등)

### 9) 인기 찜 랭킹 조회

- `GET /api/rankings/wishlist/popular`
- Query
  - `limit` (default: `10`, min: `1`, max: `100`)
- 설명
  - Redis Sorted Set(`ranking:wishlist:popular`)의 점수 기준 내림차순
  - 점수는 현재 찜 수(찜 +1, 해제 -1)
  - 조회 시 비활성/삭제 상품은 필터링
