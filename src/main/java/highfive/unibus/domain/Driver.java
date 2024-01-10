package highfive.unibus.domain;

import highfive.unibus.dto.passenger.StationDto;
import highfive.unibus.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
public class Driver {

    private String busId;
    private String prevStationOrd;
    private Timer timer;

    private final DriverService driverService;

    public Driver(String busId, Timer timer, DriverService driverService) {
        this.busId = busId;
        this.timer = timer;
        this.prevStationOrd = "00000";
        this.driverService = driverService;
    }

    public void timerStart() {
        StationDto s = new StationDto("plz", "plz");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                driverService.send();
                System.out.println("10초");
                driverService.getNextStationInfo(Driver.this);
            }
        },0, 30000);
    }

    public void timerFinish() {
        timer.cancel();
    }

    public void updateStationOrd(String stationOrd) {
        this.prevStationOrd = stationOrd;
    }

    public String getBusId() {
        return busId;
    }

    public String getPrevStationOrd() {
        return prevStationOrd;
    }

}
