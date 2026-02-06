# 에이전트 병렬 구현 체크리스트

## 사용 순서

1. 먼저 `00-common-gate-checklist.md`를 읽고 팀 공통 규칙을 확정한다.
2. 각 에이전트는 본인 파일(`agent-*.md`)만 책임지고 진행한다.
3. 공통 계약(API/DTO/이벤트)은 하루 1회 동기화한다.
4. 머지 전에는 각 파일의 `머지 전 확인` 항목을 모두 체크한다.

## 파일 목록

- `00-common-gate-checklist.md`
- `agent-a-product-admin-checklist.md`
- `agent-b-wishlist-ranking-checklist.md`
- `agent-c-cart-checklist.md`
- `agent-d-order-payment-checklist.md`
- `agent-e-llm-recommendation-checklist.md`
- `agent-f-integration-review-checklist.md`

## 권장 브랜치 네이밍

- `feat/agent-a-product-admin`
- `feat/agent-b-wishlist-ranking`
- `feat/agent-c-cart`
- `feat/agent-d-order-payment`
- `feat/agent-e-llm-recommendation`
- `chore/agent-f-integration-review`
