# API 명세 초안 (Agent A, B, C, D)

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

## 장바구니 API (Agent C)

### 10) 장바구니 조회

- `GET /api/cart/items`
- Header
  - `X-Member-Id` (필수, 1 이상)
- 설명
  - 회원 장바구니 항목 목록과 총액을 반환
  - 응답 항목: `productId`, `name`, `price`, `quantity`, `lineTotal`
  - 총액: `cartTotal`
  - 비활성/삭제 상품은 조회 응답에서 제외

### 11) 장바구니 담기

- `POST /api/cart/items`
- Header
  - `X-Member-Id` (필수, 1 이상)
- Request

```json
{
  "productId": 1,
  "quantity": 2
}
```

- 설명
  - 활성(`ACTIVE`) 상품만 담기 가능
  - 동일 상품 재요청 시 기존 수량에 합산

### 12) 장바구니 수량 변경

- `PATCH /api/cart/items/{productId}`
- Header
  - `X-Member-Id` (필수, 1 이상)
- Request

```json
{
  "quantity": 3
}
```

- 설명
  - 활성(`ACTIVE`) 상품만 변경 가능
  - 대상 상품이 장바구니에 없으면 신규 항목으로 추가

### 13) 장바구니 단건 삭제

- `DELETE /api/cart/items/{productId}`
- Header
  - `X-Member-Id` (필수, 1 이상)

### 14) 장바구니 전체 비우기

- `DELETE /api/cart/items`
- Header
  - `X-Member-Id` (필수, 1 이상)

## 주문/결제 API (Agent D)

### 15) 주문 생성(checkout)

- `POST /api/orders/checkout`
- Header
  - `X-Member-Id` (필수, 1 이상)
- 설명
  - 회원 장바구니를 주문으로 생성
  - checkout 시점에 재고를 선차감
  - 장바구니가 비어 있으면 실패

### 16) 주문 결제(pay)

- `POST /api/orders/{orderId}/pay`
- Header
  - `X-Member-Id` (필수, 1 이상)
- Request

```json
{
  "paymentToken": "CARD_20260207_0001"
}
```

- 설명
  - `paymentToken` 규칙으로 모사 결제 처리
  - `FAIL_` 접두어: 결제 실패
  - `TIMEOUT_` 접두어: 타임아웃(1회 재시도 후 실패 시 결제 실패 처리)
  - 결제 실패 시 주문 상태를 `PAYMENT_FAILED`로 변경하고 선차감 재고를 복원
  - 이미 `PAID` 주문은 멱등 성공

### 17) 주문 상세 조회

- `GET /api/orders/{orderId}`
- Header
  - `X-Member-Id` (필수, 1 이상)
- 설명
  - 주문자 본인 소유 주문만 조회 가능
  - 소유자가 아니거나 미존재 주문이면 `ORDER-404` 반환

## 주문 상태 전이 규칙

- `PENDING_PAYMENT` -> `PAID`: 결제 성공
- `PENDING_PAYMENT` -> `PAYMENT_FAILED`: 결제 실패/타임아웃 최종 실패
- `PAID` -> `PAID`: 결제 재호출 시 멱등 성공
- `PAYMENT_FAILED` -> (결제 불가): `ORDER-400-STATE`
