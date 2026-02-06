# Agent C 체크리스트: 장바구니

## 작업 범위

- 장바구니 조회
- 장바구니 담기/수량 수정/삭제

## 선행조건

- [ ] `00-common-gate-checklist.md` 확인 완료
- [ ] Agent A의 상품 조회 계약 확인

## 구현 체크리스트

### Domain

- [ ] `Cart` / `CartItem` 모델 구현
- [ ] 수량 유효성 규칙 구현(1 이상)
- [ ] 동일 상품 담기 시 수량 합산 또는 덮어쓰기 정책 확정

### Application

- [ ] 장바구니 조회 유스케이스 구현
- [ ] 상품 추가 유스케이스 구현
- [ ] 수량 변경 유스케이스 구현
- [ ] 단건 삭제/전체 비우기 유스케이스 구현

### Interface

- [ ] `GET /api/cart/items` 구현
- [ ] `POST /api/cart/items` 구현
- [ ] `PATCH /api/cart/items/{productId}` 구현
- [ ] `DELETE /api/cart/items/{productId}` 구현
- [ ] `DELETE /api/cart/items` 구현

### Infrastructure

- [ ] 장바구니 Repository/JPA Entity 구현
- [ ] 회원 기준 조회 쿼리 구현
- [ ] 상품 비활성/미존재 시 처리 전략 반영

### Test

- [ ] 도메인 테스트: 수량 규칙
- [ ] 서비스 테스트: 추가/수정/삭제/조회
- [ ] API 테스트: 장바구니 엔드포인트
- [ ] 실패 테스트: 잘못된 수량, 미존재 상품

## 완료 산출물

- [ ] 주문(Agent D) 연계를 위한 장바구니 응답 계약 문서화
- [ ] 수량 정책 문서화(합산/덮어쓰기)

## 머지 전 확인

- [ ] `./gradlew test` 통과
- [ ] Agent D와 checkout 입력 계약 동기화 완료
