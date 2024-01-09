package highfive.unibus.controller;

import highfive.unibus.common.ApiResponse;
import highfive.unibus.domain.Driver;
import highfive.unibus.dto.passenger.*;
import highfive.unibus.service.PassengerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@AllArgsConstructor
@RequestMapping("/passenger")
public class PassengerController {

    private final PassengerService passengerService;
    private static HashMap<String, Driver> drivers = new HashMap<>();

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

    @PostMapping("/reservation")
    public ApiResponse reserveBus(@RequestBody BusReservationDto busReservationDto) {
        passengerService.reserveBus(busReservationDto);

        return ApiResponse.builder()
                .code(200)
                .message("버스 예약 성공")
                .build();
    }

}
