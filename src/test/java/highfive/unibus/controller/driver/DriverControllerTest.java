package highfive.unibus.controller.driver;

import highfive.unibus.dto.driver.BusInfoDto;
import highfive.unibus.dto.driver.DriverNotificationDto;
import highfive.unibus.repository.StationPassengerInfoRepository;
import highfive.unibus.service.DriverService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class DriverControllerTest {

    @Autowired DriverService driverService;
    @Autowired
    StationPassengerInfoRepository repository;
    @Autowired
    EntityManager em;

    @Test
    void getDriverNotificationTest() {

        //given
        StationPassengerId id = new StationPassengerId(111033115, 12143);
        StationPassengerInfo info = StationPassengerInfo.builder()
                .stationPassengerId(id)
                .stationName("선진운수종점")
                .physicalDisabilityNum(2)
                .visualDisabilityNum(1)
                .getOffNum(3)
                .build();
        repository.save(info);

        em.flush();
        em.clear();

        BusInfoDto busInfoDto = new BusInfoDto("111033115", "0");

        //when
        DriverNotificationDto driverNotification = driverService.getNextStationInfo(busInfoDto);

        //then
        System.out.println(driverNotification.getPhysicalDisabilityNum());
        System.out.println(driverNotification.getVisualDisabilityNum());
    }

    @Test
    void getStationPassengerInfoTest() {

        //given
        StationPassengerId id = new StationPassengerId(111033115, 12143);
        StationPassengerInfo info = StationPassengerInfo.builder()
                .stationPassengerId(id)
                .stationName("선진운수종점")
                .physicalDisabilityNum(2)
                .visualDisabilityNum(1)
                .getOffNum(3)
                .build();
        repository.save(info);

        //when
        StationPassengerInfo result = repository.findById(id).get();
        System.out.println(result.getPhysicalDisabilityNum());
        System.out.println(result.getGetOffNum());
    }

}