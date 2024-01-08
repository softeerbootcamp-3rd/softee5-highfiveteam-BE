package highfive.unibus.controller.driver;

import highfive.unibus.dto.driver.BusInfoDto;
import highfive.unibus.dto.driver.DriverNotificationDto;
import highfive.unibus.repository.StationPassengerInfoRepository;
import highfive.unibus.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final StationPassengerInfoRepository repository;

    @GetMapping("/driver/next")
    public DriverNotificationDto getDriverNotification(@RequestBody BusInfoDto dto) {
        return driverService.getNextStationInfo(dto);
    }

}
