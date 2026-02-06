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
import me.gogradually.toycommerce.application.product.AdminProductService;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.interfaces.dto.product.CreateProductRequest;
import me.gogradually.toycommerce.interfaces.dto.product.ProductResponse;
import me.gogradually.toycommerce.interfaces.dto.product.UpdateProductRequest;
import me.gogradually.toycommerce.interfaces.dto.product.UpdateProductStockRequest;
import me.gogradually.toycommerce.interfaces.utils.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
@Tag(name = "Admin Products", description = "관리자 상품/재고 관리 API")
public class AdminProductController {

    private final AdminProductService adminProductService;

    @PostMapping
    @Operation(summary = "상품 생성", description = "관리자가 신규 상품을 생성합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 값",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody @Valid CreateProductRequest request
    ) {
        ProductDetailInfo created = adminProductService.createProduct(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ProductResponse.from(created)));
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "상품명, 가격, 상태를 수정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<ProductResponse> updateProduct(
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable @Min(value = 1, message = "productId는 1 이상이어야 합니다.") Long productId,
            @RequestBody @Valid UpdateProductRequest request
    ) {
        ProductDetailInfo updated = adminProductService.updateProduct(productId, request.toCommand());
        return ApiResponse.success(ProductResponse.from(updated));
    }

    @PatchMapping("/{productId}/stock")
    @Operation(summary = "재고 수정", description = "상품 재고 수량을 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<ProductResponse> updateStock(
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable @Min(value = 1, message = "productId는 1 이상이어야 합니다.") Long productId,
            @RequestBody @Valid UpdateProductStockRequest request
    ) {
        ProductDetailInfo updated = adminProductService.updateStock(productId, request.toCommand());
        return ApiResponse.success(ProductResponse.from(updated));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<Void> deleteProduct(
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable @Min(value = 1, message = "productId는 1 이상이어야 합니다.") Long productId
    ) {
        adminProductService.deleteProduct(productId);
        return ApiResponse.success(null);
    }
}
