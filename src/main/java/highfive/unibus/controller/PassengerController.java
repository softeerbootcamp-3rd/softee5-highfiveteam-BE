package highfive.unibus.controller;

import highfive.unibus.common.ApiResponse;
import highfive.unibus.dto.passenger.AvailableBusDto;
import highfive.unibus.dto.passenger.AvailableBusRequestDto;
import highfive.unibus.service.PassengerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@AllArgsConstructor
@RequestMapping("/passenger")
public class PassengerController {

    private final PassengerService passengerService;

    @GetMapping("/buslist")
    public ApiResponse searchAvailableBusList(@RequestBody AvailableBusRequestDto availableBusRequestDto) {
        ArrayList<AvailableBusDto> data = passengerService.getBusListByStationNames(availableBusRequestDto.getDeparture(), availableBusRequestDto.getDestination());

        String message;
        if (data == null || data.size() == 0) {
            message = "현재 탑승 가능한 버스가 없습니다.";
        } else {
            message = "탑승 가능한 버스 조회 성공";
        }

        return ApiResponse.builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

}
