package kr.hhplus.ecommerce.interfaces.point;

import jakarta.validation.Valid;
import kr.hhplus.ecommerce.domain.point.UserPointCommand;
import kr.hhplus.ecommerce.domain.point.UserPointService;
import kr.hhplus.ecommerce.domain.point.UserPointVo;
import kr.hhplus.ecommerce.interfaces.UserPointRequest;
import kr.hhplus.ecommerce.interfaces.common.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/points")
@RequiredArgsConstructor
public class UserPointController {
    private final UserPointService userPointService;
    
    @PostMapping("/charge")
    ApiResponse<UserPointResponse.UserPoint> charge(@Valid @RequestBody UserPointRequest.Charge request) {
        UserPointCommand.Charge command = new UserPointCommand.Charge(request.userId(), request.amount());
        UserPointVo result = userPointService.charge(command);

        return ApiResponse.success(
            UserPointResponse.UserPoint.from(result)
        );
    }
} 