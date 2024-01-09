package highfive.unibus.domain;

import highfive.unibus.service.DriverService;
import lombok.RequiredArgsConstructor;

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
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                driverService.getNextStationInfo(Driver.this);
            }
        },0, 10000);
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
