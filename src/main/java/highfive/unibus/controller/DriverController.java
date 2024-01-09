package highfive.unibus.controller;

import highfive.unibus.domain.Driver;
import highfive.unibus.dto.driver.BusInfoDto;
import highfive.unibus.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;
    private static Map<String, Driver> drivers = new HashMap<>();

    @GetMapping("/driver/start")
    public void driveStart(@RequestBody BusInfoDto dto) {
        Driver driver = new Driver(dto.getBusId(), new Timer(), driverService);
        drivers.put(dto.getBusId(), driver);
        log.info(dto.getBusId() + " bus drive start");
        driver.timerStart();
    }

    @GetMapping("/driver/stop")
    public void driveStop(@RequestBody BusInfoDto dto) {
        Driver driver = drivers.get(dto.getBusId());
        driver.timerFinish();
        log.info(dto.getBusId() + " bus drive stop");
    }

}
