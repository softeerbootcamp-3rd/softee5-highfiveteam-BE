package highfive.unibus.domain;

import lombok.AllArgsConstructor;

import java.util.Timer;
import java.util.TimerTask;

@AllArgsConstructor
public class Driver {

    private String busId;
    private String prevStationOrd;
    private Timer timer;

    public void timerStart() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 10초마다 실행할 코드
                // api 호출하고, prevStationOrd와 비교
            }
        },0, 10000);
    }

    public void timerFinish() {
        timer.cancel();
    }

    public Driver(String busId, Timer timer) {
        this.busId = busId;
        this.timer = timer;
        this.prevStationOrd = "00000";
    }

}
