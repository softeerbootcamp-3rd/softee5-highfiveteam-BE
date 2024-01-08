package highfive.unibus.dto.passenger;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvailableBusDto {

    private int busId;
    private String busNum;
    private String vehicleNum;
    private Congestion congestion;
    private BusType busType;
    private String arrivalTime;

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

enum BusType {
    간선버스(3),
    지선버스(4),
    순환버스(5);

    private final int length;

    BusType(int length) {
        this.length = length;
    }
}