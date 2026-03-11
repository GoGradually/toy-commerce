package me.gogradually.toycommerce.interfaces.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import me.gogradually.toycommerce.application.product.ProductQueryService;
import me.gogradually.toycommerce.application.product.dto.ProductDetailInfo;
import me.gogradually.toycommerce.application.product.dto.ProductPageInfo;
import me.gogradually.toycommerce.interfaces.dto.product.ProductListResponse;
import me.gogradually.toycommerce.interfaces.dto.product.ProductResponse;
import me.gogradually.toycommerce.interfaces.utils.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Tag(name = "Public Products", description = "사용자 상품 조회 API")
public class ProductController {

    private final ProductQueryService productQueryService;

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "활성 상태의 상품 목록을 페이지네이션으로 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 값",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<ProductListResponse> getProducts(
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
            @Parameter(description = "페이지 크기(1~100)", example = "20")
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "size는 1 이상이어야 합니다.") int size,
            @Parameter(description = "정렬 필드(id, name, price, createdAt)", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방향(asc, desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String direction
    ) {
        ProductPageInfo productPageInfo = productQueryService.getProducts(page, size, sortBy, direction);
        return ApiResponse.success(ProductListResponse.from(productPageInfo));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 활성 상품 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품 미존재",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<ProductResponse> getProduct(
            @Parameter(description = "상품 ID", example = "1")
            @PathVariable @Min(value = 1, message = "productId는 1 이상이어야 합니다.") Long productId
    ) {
        ProductDetailInfo product = productQueryService.getProduct(productId);
        return ApiResponse.success(ProductResponse.from(product));
    }
}
