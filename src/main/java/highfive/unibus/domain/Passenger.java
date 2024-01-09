package highfive.unibus.domain;

import lombok.AllArgsConstructor;

import java.util.Timer;
import java.util.TimerTask;

@AllArgsConstructor
public class Passenger {

    private String busId;
    private String destinationNum;
    private Timer timer;

    public void timerStart() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 10초마다 실행할 코드
                // api 호출하고, destinationNum과 같은지 비교해서 도착 여부 알림
            }
        },0, 10000);
    }

    public void timerFinish() {
        timer.cancel();
    }

}
