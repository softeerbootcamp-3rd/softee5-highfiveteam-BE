package highfive.unibus.domain;

import highfive.unibus.dto.passenger.BusReservationDto;
import highfive.unibus.service.PassengerService;
import lombok.RequiredArgsConstructor;

import java.util.Timer;
import java.util.TimerTask;

@RequiredArgsConstructor
public class Passenger {

    private String busId;
    private String departureStationOrd;
    private String destinationStatioOrd;
    private boolean beforeRide;
    private Timer timer;
    private final PassengerService passengerService;

    public void timerStart() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (beforeRide) {
                    // 탑승할 역에서 알림
                    System.out.println("탑승할 역 체크");
                    if (passengerService.notifyDepartureStation(busId, departureStationOrd)) {
                        System.out.println("탑승역 지남");
                        beforeRide = false;
                    }
                } else {
                    // 내릴 역에서 알림
                    System.out.println("내릴 역 체크");
                    if (passengerService.notifyDepartureStation(busId, destinationStatioOrd)) {
                        System.out.println("내릴역 지남");
                        timerFinish();
                    }
                }
            }
        },0, 30000);
    }

    public void timerFinish() {
        timer.cancel();
    }

    public Passenger(BusReservationDto busReservationDto, PassengerService passengerService) {
        this.busId = busReservationDto.getBusId();
        this.departureStationOrd = busReservationDto.getDepartureStationOrd();
        this.destinationStatioOrd = busReservationDto.getDestinationStationOrd();
        this.beforeRide = true;
        this.timer = new Timer();
        this.passengerService = passengerService;
    }

}
