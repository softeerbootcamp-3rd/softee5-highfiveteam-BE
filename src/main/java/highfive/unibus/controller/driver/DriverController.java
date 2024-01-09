package highfive.unibus.controller.driver;

import highfive.unibus.domain.Driver;
import highfive.unibus.dto.driver.BusInfoDto;
import highfive.unibus.dto.driver.DriverNotificationDto;
import highfive.unibus.repository.StationPassengerInfoRepository;
import highfive.unibus.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Timer;

@RestController
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private final StationPassengerInfoRepository repository;
    private static HashMap<String, Driver> drivers = new HashMap<>();

    @GetMapping("/driver/next")
    public DriverNotificationDto getDriverNotification(@RequestBody BusInfoDto dto) {
        return driverService.getNextStationInfo(dto);
    }

    @GetMapping("/driver/start")
    public void everySecond(@RequestBody BusInfoDto dto) {
        Driver driver = new Driver(dto.getBusId(), new Timer());
        drivers.put(dto.getBusId(), driver);

        driver.timerStart();
    }

    @GetMapping("/driver/stop")
    public void stop(@RequestBody BusInfoDto dto) {
        Driver driver = drivers.get(dto.getBusId());
        driver.timerFinish();
        System.out.println("stop");
    }


}
