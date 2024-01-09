package highfive.unibus.controller;

import highfive.unibus.common.ApiResponse;
import highfive.unibus.dto.passenger.AvailableBusDto;
import highfive.unibus.dto.passenger.AvailableBusRequestDto;
import highfive.unibus.dto.passenger.StationDto;
import highfive.unibus.dto.passenger.StationRequestDto;
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

    @GetMapping("/station")
    public ApiResponse searchStationList(@RequestBody StationRequestDto stationRequestDto) {
        ArrayList<StationDto> stationDtos = passengerService.getStationsByName(stationRequestDto.getSearchWord());

        String message;
        if (stationDtos == null || stationDtos.size() == 0) {
            message = "현재 탑승 가능한 버스가 없습니다.";
        } else {
            message = "탑승 가능한 버스 조회 성공";
        }

        return ApiResponse.builder()
                .code(200)
                .message(message)
                .data(stationDtos)
                .build();
    }

    @GetMapping("/buslist")
    public ApiResponse searchAvailableBusList(@RequestBody AvailableBusRequestDto availableBusRequestDto) {
        ArrayList<AvailableBusDto> availableBusDtos = passengerService.getAvailableBusListByStationNums(availableBusRequestDto.getDepartureStationNum(), availableBusRequestDto.getDestinationStationNum());

        String message;
        if (availableBusDtos == null || availableBusDtos.size() == 0) {
            message = "현재 탑승 가능한 버스가 없습니다.";
        } else {
            message = "탑승 가능한 버스 조회 성공";
        }

        return ApiResponse.builder()
                .code(200)
                .message(message)
                .data(availableBusDtos)
                .build();
    }

}
