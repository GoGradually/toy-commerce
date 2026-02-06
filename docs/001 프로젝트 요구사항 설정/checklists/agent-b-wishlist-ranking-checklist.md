# Agent B 체크리스트: 찜 기능 + 인기 찜 랭킹

## 작업 범위

- 상품 찜/찜 해제
- 인기 찜 랭킹 조회

## 선행조건

- [ ] `00-common-gate-checklist.md` 확인 완료
- [ ] Agent A의 상품 식별자/상태 계약 확인

## 구현 체크리스트

### Domain

- [ ] `Wishlist` 모델 및 중복 방지 규칙 구현
- [ ] 찜 수 집계 기준 합의

### Application

- [ ] 찜 추가 유스케이스 구현(멱등성 보장)
- [ ] 찜 해제 유스케이스 구현(멱등성 보장)
- [ ] 인기 랭킹 조회 유스케이스 구현

### Interface

- [ ] `POST /api/products/{productId}/wishlist` 구현
- [ ] `DELETE /api/products/{productId}/wishlist` 구현
- [ ] `GET /api/rankings/wishlist/popular` 구현

### Infrastructure

- [ ] 찜 테이블/Repository 구현
- [ ] 랭킹 집계 전략 구현(redis Sorted Set 이용)
- [ ] `(member_id, product_id)` 유니크 제약 적용

### Test

- [ ] 도메인 테스트: 중복 찜 방지
- [ ] 서비스 테스트: 찜/해제 멱등성
- [ ] API 테스트: 찜/랭킹 조회
- [ ] 실패 테스트: 미존재 상품, 비활성 상품 처리

## 완료 산출물

- [ ] 랭킹 계산 방식 문서화(정확성/지연 시간)
- [ ] 랭킹 API 파라미터(limit 등) 문서화

## 머지 전 확인

- [ ] `./gradlew test` 통과
- [ ] Agent A와 상품 상태 반영 규칙 재확인
