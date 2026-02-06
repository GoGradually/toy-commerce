package me.gogradually.toycommerce.interfaces.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "공통 API 응답")
public record ApiResponse<T>(
        @Schema(description = "요청 성공 여부", example = "true")
        boolean success,
        @Schema(description = "응답 데이터")
        T data,
        @Schema(description = "에러 정보")
        ErrorBody error
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorBody(code, message));
    }

    @Schema(description = "에러 상세")
    public record ErrorBody(String code, String message) {
    }
}
