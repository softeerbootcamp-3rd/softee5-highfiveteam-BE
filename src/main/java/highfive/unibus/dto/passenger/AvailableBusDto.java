package highfive.unibus.dto.passenger;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvailableBusDto {

    private String busId;
    private String busNum;
    private String vehicleNum;
    private Congestion congestion;
    private String busType;
    private String arrivalTime;
    private String orderInRoute;

}

enum Congestion {
    없음(0),
    여유(3),
    보통(4),
    혼잡(5),
    매우혼잡(6);

    private final int state;

    Congestion(int state) {
        this.state = state;
    }
}