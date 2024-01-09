package highfive.unibus.domain;

import highfive.unibus.dto.passenger.BusReservationDto;
import lombok.AllArgsConstructor;

import java.util.Timer;
import java.util.TimerTask;

@AllArgsConstructor
public class Passenger {

    private String busId;
    private String departureStationNum;
    private String destinationStatioNum;
    private boolean inBus;
    private Timer timer;

    public void timerStart() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (inBus) {
                    // 내릴 역에서 알림
                } else {
                    // 탑승할 역에서 알림

                }
                // 10초마다 실행할 코드
                // api 호출하고, destinationNum과 같은지 비교해서 도착 여부 알림
            }
        },0, 10000);
    }

    public void timerFinish() {
        timer.cancel();
    }

    public Passenger(BusReservationDto busReservationDto) {
        this.busId = busReservationDto.getBusId();
        this.departureStationNum = busReservationDto.getDepartureStationNum();
        this.destinationStatioNum = busReservationDto.getDestinationStationNum();
        this.inBus = false;
        this.timer = new Timer();
    }

}
