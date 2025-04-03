package kr.hhplus.ecommerce.controller;

import jakarta.validation.Valid;
import kr.hhplus.ecommerce.common.web.ApiResponse;
import kr.hhplus.ecommerce.controller.request.UserPointRequest;
import kr.hhplus.ecommerce.controller.response.UserPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/point")
public class UserPointController {
    /**
     * TODO - 포인트 조회
     */
    @GetMapping
    ApiResponse<UserPointResponse.UserPoint> get(
        @PathVariable long userId
    ) {
        return ApiResponse.success(
            new UserPointResponse.UserPoint(1L, 1L, 0)
        );
    }

    /**
     * TODO - 포인트 충전
     */
    @PostMapping("/charge")
    public ApiResponse<UserPointResponse.UserPoint> charge(
        @PathVariable long userId,
        @Valid @RequestBody UserPointRequest.Charge request
    ) {
        return ApiResponse.success(
            new UserPointResponse.UserPoint(1L, 1L, 0)
        );
    }
}
