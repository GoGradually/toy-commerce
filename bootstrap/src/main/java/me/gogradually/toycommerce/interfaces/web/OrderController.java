package me.gogradually.toycommerce.interfaces.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.order.OrderService;
import me.gogradually.toycommerce.application.order.dto.*;
import me.gogradually.toycommerce.interfaces.dto.order.*;
import me.gogradually.toycommerce.interfaces.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "주문/결제 API")
public class OrderController {

    private static final String MEMBER_ID_HEADER = "X-Member-Id";

    private final OrderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "주문 생성", description = "회원의 장바구니를 주문으로 생성하고 재고를 차감합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "기존 미결 주문 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 또는 주문 생성 실패",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<CheckoutOrderResponse>> checkout(
            @Parameter(description = "회원 ID 헤더", example = "1001")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId
    ) {
        CheckoutOrderResult result = orderService.checkout(memberId);
        HttpStatus status = result.created() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status)
                .body(ApiResponse.success(CheckoutOrderResponse.from(result.order())));
    }

    @PostMapping("/{orderId}/details")
    @Operation(summary = "주문 정보 입력 완료", description = "배송지, 쿠폰, 결제 수단을 저장하고 주문 상태를 정보 입력 완료로 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 정보 입력 완료 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 또는 잘못된 주문 상태",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "주문 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<CompleteOrderDetailsResponse> completeOrderDetails(
            @Parameter(description = "회원 ID 헤더", example = "1001")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "주문 ID", example = "1")
            @PathVariable @Min(value = 1, message = "orderId는 1 이상이어야 합니다.") Long orderId,
            @RequestBody @Valid CompleteOrderDetailsRequest request
    ) {
        CompleteOrderDetailsInfo info = orderService.completeOrderDetails(memberId, orderId, request.toCommand());
        return ApiResponse.success(CompleteOrderDetailsResponse.from(info));
    }

    @PostMapping("/{orderId}/pay")
    @Operation(summary = "주문 결제", description = "결제를 모사하여 주문 상태를 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "결제 성공/실패 또는 멱등 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 상태",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "주문 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<PayOrderResponse> pay(
            @Parameter(description = "회원 ID 헤더", example = "1001")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "주문 ID", example = "1")
            @PathVariable @Min(value = 1, message = "orderId는 1 이상이어야 합니다.") Long orderId,
            @RequestBody @Valid PayOrderRequest request
    ) {
        PayOrderInfo info = orderService.pay(memberId, orderId, request.toCommand());
        return ApiResponse.success(PayOrderResponse.from(info));
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "미결 주문을 취소하고 예약된 재고를 복구합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "취소 성공 또는 멱등 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 상태",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "주문 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<CancelOrderResponse> cancel(
            @Parameter(description = "회원 ID 헤더", example = "1001")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "주문 ID", example = "1")
            @PathVariable @Min(value = 1, message = "orderId는 1 이상이어야 합니다.") Long orderId
    ) {
        CancelOrderInfo info = orderService.cancel(memberId, orderId);
        return ApiResponse.success(CancelOrderResponse.from(info));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 상세와 항목 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "주문 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<OrderDetailResponse> getOrder(
            @Parameter(description = "회원 ID 헤더", example = "1001")
            @RequestHeader(MEMBER_ID_HEADER) @Min(value = 1, message = "memberId는 1 이상이어야 합니다.") Long memberId,
            @Parameter(description = "주문 ID", example = "1")
            @PathVariable @Min(value = 1, message = "orderId는 1 이상이어야 합니다.") Long orderId
    ) {
        OrderDetailInfo info = orderService.getOrder(memberId, orderId);
        return ApiResponse.success(OrderDetailResponse.from(info));
    }
}
