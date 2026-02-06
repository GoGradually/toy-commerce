# Agent A 체크리스트: 상품 조회 + 관리자 상품/재고 CRUD

## 작업 범위

- 상품 리스트/상세
- 관리자 상품 CRUD
- 관리자 재고 수정

## 선행조건

- [ ] `00-common-gate-checklist.md` 확인 완료
- [ ] 상품 도메인 기본 필드 합의 완료(id, name, price, stock, status)

## 구현 체크리스트

### Domain

- [ ] `Product` aggregate 생성
- [ ] 가격/재고 유효성 규칙 구현(음수 방지)
- [ ] 상품 상태 규칙 구현(활성/비활성 등)

### Application

- [ ] 상품 목록 조회 유스케이스 구현
- [ ] 상품 상세 조회 유스케이스 구현
- [ ] 상품 생성/수정/삭제 유스케이스 구현
- [ ] 재고 변경 유스케이스 구현

### Interface

- [ ] `GET /api/products` 구현
- [ ] `GET /api/products/{productId}` 구현
- [ ] `POST /api/admin/products` 구현
- [ ] `PATCH /api/admin/products/{productId}` 구현
- [ ] `PATCH /api/admin/products/{productId}/stock` 구현
- [ ] `DELETE /api/admin/products/{productId}` 구현

### Infrastructure

- [ ] 상품 Repository/JPA Entity 구현
- [ ] 목록 조회 정렬/페이지네이션 쿼리 구현
- [ ] 상품 조회 성능을 위한 인덱스 검토

### Test

- [ ] 도메인 테스트: 가격/재고 검증
- [ ] 서비스 테스트: CRUD/재고 변경 유스케이스
- [ ] API 테스트: 목록/상세/관리자 엔드포인트
- [ ] 실패 테스트: 미존재 상품, 잘못된 요청 값

## 완료 산출물

- [ ] API 명세 초안 업데이트
- [ ] 예외 코드 목록 업데이트

## 머지 전 확인

- [ ] `./gradlew test` 통과
- [ ] Agent C, D가 사용하는 상품 조회 계약 변경 여부 공유
