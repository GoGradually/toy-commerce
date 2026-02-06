# Agent D 체크리스트: 주문/결제(모사)

## 작업 범위

- 주문 생성(checkout)
- 결제 모사 처리
- 주문 상태 조회

## 선행조건

- [ ] `00-common-gate-checklist.md` 확인 완료
- [ ] Agent C의 장바구니 계약 확정
- [ ] Agent A의 재고/상품 상태 계약 확정

## 구현 체크리스트

### Domain

- [ ] `Order` / `OrderItem` 모델 구현
- [ ] 주문 상태 enum 및 상태 전이 규칙 구현
- [ ] 주문 금액 계산 규칙 구현

### Application

- [ ] checkout 유스케이스 구현(장바구니 -> 주문)
- [ ] 재고 차감 처리 구현(트랜잭션 경계 명확화)
- [ ] 결제 모사 유스케이스 구현(성공/실패)
- [ ] 주문 상세 조회 유스케이스 구현

### Interface

- [ ] `POST /api/orders/checkout` 구현
- [ ] `POST /api/orders/{orderId}/pay` 구현
- [ ] `GET /api/orders/{orderId}` 구현

### Infrastructure

- [ ] 주문 Repository/JPA Entity 구현
- [ ] 결제 모사 어댑터 구현(infrastructure/web 또는 adapter)
- [ ] 결제 타임아웃/실패 시 재시도 정책 합의

### Test

- [ ] 도메인 테스트: 상태 전이/금액 계산
- [ ] 서비스 테스트: checkout/결제 성공/결제 실패
- [ ] API 테스트: 주문/결제/조회
- [ ] 실패 테스트: 재고 부족, 빈 장바구니, 중복 결제

## 완료 산출물

- [ ] 주문 상태 전이 문서 업데이트
- [ ] 결제 모사 규칙(성공 조건/실패 조건) 문서화

## 머지 전 확인

- [ ] `./gradlew test` 통과
- [ ] Agent A(재고), Agent C(장바구니)와 회귀 확인 완료
