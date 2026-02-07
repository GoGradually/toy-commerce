# Agent D 체크리스트: 주문/결제(모사)

## 작업 범위

- 주문 생성(checkout)
- 결제 모사 처리
- 주문 상태 조회

## 선행조건

- [x] `00-common-gate-checklist.md` 확인 완료
- [x] Agent C의 장바구니 계약 확정
- [x] Agent A의 재고/상품 상태 계약 확정

## 구현 체크리스트

### Domain

- [x] `Order` / `OrderItem` 모델 구현
- [x] 주문 상태 enum 및 상태 전이 규칙 구현
- [x] 주문 금액 계산 규칙 구현

### Application

- [x] checkout 유스케이스 구현(장바구니 -> 주문)
- [x] 재고 차감 처리 구현(트랜잭션 경계 명확화)
- [x] 결제 모사 유스케이스 구현(성공/실패)
- [x] 주문 상세 조회 유스케이스 구현

### Interface

- [x] `POST /api/orders/checkout` 구현
- [x] `POST /api/orders/{orderId}/pay` 구현
- [x] `GET /api/orders/{orderId}` 구현

### Infrastructure

- [x] 주문 Repository/JPA Entity 구현
- [x] 결제 모사 어댑터 구현(infrastructure/web 또는 adapter)
- [x] 결제 타임아웃/실패 시 재시도 정책 합의

### Test

- [x] 도메인 테스트: 상태 전이/금액 계산
- [x] 서비스 테스트: checkout/결제 성공/결제 실패
- [x] API 테스트: 주문/결제/조회
- [x] 실패 테스트: 재고 부족, 빈 장바구니, 중복 결제

## 완료 산출물

- [x] 주문 상태 전이 문서 업데이트
- [x] 결제 모사 규칙(성공 조건/실패 조건) 문서화

### 정책/계약 메모

- 재고 차감 시점: checkout 시점 선차감, 결제 실패 시 복원.
- 결제 모사 규칙: `paymentToken` 접두어 기반(`FAIL_` 실패, `TIMEOUT_` 타임아웃).
- 타임아웃 재시도: 1회 즉시 재시도 후 실패 시 `PAYMENT_FAILED`.
- 중복 결제 정책: `PAID` 주문 재결제 호출은 멱등 성공.
- 주문 접근 정책: `X-Member-Id`와 주문 소유자 불일치 시 `ORDER-404`.

### 구현 결정사항 상세

- 주문 상태값은 `PENDING_PAYMENT`, `PAID`, `PAYMENT_FAILED` 3가지를 사용한다.
- 상태 전이는 `PENDING_PAYMENT -> PAID`, `PENDING_PAYMENT -> PAYMENT_FAILED`만 허용한다.
- `PAYMENT_FAILED` 상태에서 결제 재시도는 `ORDER-400-STATE`로 거절한다.
- `PAID` 상태 결제 재호출은 상태 변경 없이 성공 응답(멱등)으로 처리한다.
- 주문 금액은 주문 아이템의 `lineTotal` 합계와 반드시 일치해야 한다.
- 주문 아이템 `lineTotal`은 `unitPrice * quantity`로 강제 계산/검증한다.
- `OrderItem` 유효성 검증 예외는 `IllegalArgumentException` 대신 도메인 커스텀 예외(`InvalidOrderItemException`)를 사용한다.

### API 계약 상세

- `POST /api/orders/checkout`는 본문 없이 `X-Member-Id`만 받아 주문을 생성한다.
- `POST /api/orders/checkout` 성공 시 HTTP `201`과 주문 요약(`orderId`, `status`, `totalAmount`, `items`)을 반환한다.
- `POST /api/orders/{orderId}/pay` 요청 본문은 `paymentToken` 필수이다.
- `POST /api/orders/{orderId}/pay` 성공 시 `paid=true`, `paymentResult=SUCCESS`를 반환한다.
- `GET /api/orders/{orderId}`는 주문 소유자만 조회 가능하다.
- 주문 소유자 불일치도 보안상 미존재와 동일하게 `ORDER-404`로 응답한다.

### 결제 모사/실패 처리 결정

- `paymentToken`이 `FAIL_`로 시작하면 결제 실패로 처리한다.
- `paymentToken`이 `TIMEOUT_`로 시작하면 타임아웃 예외를 발생시킨다.
- 타임아웃은 최대 2회 시도(최초 + 재시도 1회) 후 최종 실패 처리한다.
- 결제 최종 실패 시 주문 상태를 `PAYMENT_FAILED`로 저장하고, checkout 시 차감했던 재고를 복원한다.
- 결제 실패 응답 코드는 `PAYMENT-400-FAILED`를 사용한다.
- 결제 실패 상태/재고 복원 결과를 커밋하기 위해 `pay` 트랜잭션은 `PaymentFailedException`에 대해 롤백하지 않는다.

### 트랜잭션/동시성 결정

- checkout/pay는 각각 단일 트랜잭션 경계로 처리한다.
- checkout에서 상품 재고는 `PESSIMISTIC_WRITE` 락 조회 후 차감한다.
- pay에서 주문은 `PESSIMISTIC_WRITE` 락 조회 후 상태 전이를 수행한다.
- pay 실패 후 재고 복원 시에도 상품을 락 조회하여 동시성 충돌을 줄인다.

### 에러코드 확정

- `ORDER-404`: 주문을 찾을 수 없습니다.
- `ORDER-400-MEMBER`: memberId는 1 이상이어야 합니다.
- `ORDER-400-EMPTY-CART`: 장바구니가 비어 있습니다.
- `ORDER-400-STATE`: 현재 주문 상태에서 요청을 처리할 수 없습니다.
- `ORDER-400-STOCK`: 재고가 부족합니다.
- `PAYMENT-400-FAILED`: 결제에 실패했습니다.
- `InvalidOrderItemException`은 공통 요청 오류(`COMMON-400`)로 매핑한다.

### 검증 시나리오 확정

- checkout 성공 시 주문 생성 + 재고 차감 + 장바구니 비우기.
- checkout 실패 시 빈 장바구니/재고 부족 예외 검증.
- pay 성공 시 `PENDING_PAYMENT -> PAID` 전이 검증.
- pay 실패 시 `PENDING_PAYMENT -> PAYMENT_FAILED` + 재고 복원 검증.
- pay 타임아웃 재시도(1회) 후 성공/실패 분기 검증.
- `PAID` 중복 결제 요청 멱등 성공 검증.
- 소유자 불일치 주문 조회/결제 요청 `ORDER-404` 검증.

## 머지 전 확인

- [x] `./gradlew test` 통과
- [x] Agent A(재고), Agent C(장바구니)와 회귀 확인 완료
