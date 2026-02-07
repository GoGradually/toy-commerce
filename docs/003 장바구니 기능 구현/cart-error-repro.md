# 장바구니 에러 재현 가이드

## 목적

- 장바구니 API의 주요 실패 케이스를 자동 재현한다.
- 재현 기준은 통합 테스트 `CartErrorReproE2ETest`로 고정한다.

## 실행 명령

```bash
./gradlew test --tests "*CartErrorReproE2ETest"
```

## 재현 케이스 매트릭스

| 테스트 메서드                                                      | 요청 요약                                                   | 기대 HTTP | 기대 `error.code`     |
|--------------------------------------------------------------|---------------------------------------------------------|--------:|---------------------|
| `shouldReproduceCommon400WhenMemberIdHeaderIsInvalid`        | `GET /api/cart/items` + `X-Member-Id: 0`                |     400 | `COMMON-400`        |
| `shouldReproduceCommon400WhenProductIdInAddRequestIsInvalid` | `POST /api/cart/items` + `{"productId":0,"quantity":1}` |     400 | `COMMON-400`        |
| `shouldReproduceCommon400WhenQuantityInAddRequestIsInvalid`  | `POST /api/cart/items` + `{"productId":1,"quantity":0}` |     400 | `COMMON-400`        |
| `shouldReproduceProduct404WhenProductDoesNotExist`           | `POST /api/cart/items` + 미존재 `productId`                |     404 | `PRODUCT-404`       |
| `shouldReproduceCart400InactiveWhenProductIsInactive`        | ACTIVE 상품 생성 후 INACTIVE 변경 뒤 장바구니 담기                    |     400 | `CART-400-INACTIVE` |
| `shouldReproduceCommon400WhenProductIdPathVariableIsInvalid` | `PATCH /api/cart/items/0` + `{"quantity":3}`            |     400 | `COMMON-400`        |

## 검증 포인트

- 공통 응답 포맷 확인:
    - `success == false`
    - `error.code` 값이 케이스별 기대값과 일치
- 필요 시 `error.message`까지 함께 확인해 검증 강도를 높인다.

## 참고

- `CART-400-MEMBER`, `CART-400-PRODUCT`는 도메인 예외 코드로 정의되어 있지만,
  현재 컨트롤러 레벨 Bean Validation(`@Min`)이 먼저 동작하는 요청 경로에서는 `COMMON-400`이 반환될 수 있다.
