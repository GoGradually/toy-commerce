# Agent B 체크리스트: 찜 기능 + 인기 찜 랭킹

## 작업 범위

- 상품 찜/찜 해제
- 인기 찜 랭킹 조회

## 선행조건

- [x] `00-common-gate-checklist.md` 확인 완료
- [x] Agent A의 상품 식별자/상태 계약 확인

## 구현 체크리스트

### Domain

- [x] `Wishlist` 모델 및 중복 방지 규칙 구현
- [x] 찜 수 집계 기준 합의

### Application

- [x] 찜 추가 유스케이스 구현(멱등성 보장)
- [x] 찜 해제 유스케이스 구현(멱등성 보장)
- [x] 인기 랭킹 조회 유스케이스 구현

### Interface

- [x] `POST /api/products/{productId}/wishlist` 구현
- [x] `DELETE /api/products/{productId}/wishlist` 구현
- [x] `GET /api/rankings/wishlist/popular` 구현

### Infrastructure

- [x] 찜 테이블/Repository 구현
- [x] 랭킹 집계 전략 구현(redis Sorted Set 이용)
- [x] `(member_id, product_id)` 유니크 제약 적용

### Test

- [x] 도메인 테스트: 중복 찜 방지
- [x] 서비스 테스트: 찜/해제 멱등성
- [x] API 테스트: 찜/랭킹 조회
- [x] 실패 테스트: 미존재 상품, 비활성 상품 처리

## 완료 산출물

- [x] 랭킹 계산 방식 문서화(정확성/지연 시간)
- [x] 랭킹 API 파라미터(limit 등) 문서화

## 머지 전 확인

- [x] `./gradlew test` 통과
- [x] Agent A와 상품 상태 반영 규칙 재확인
