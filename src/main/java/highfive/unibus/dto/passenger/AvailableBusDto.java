package highfive.unibus.dto.passenger;

import lombok.Getter;

public class AvailableBusDto {

    private int busNum;
    private int vehicleNum;
    private Congestion congestion;
    private BusType busType;


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
    지선버스(4);

    private final int length;

    BusType(int length) {
        this.length = length;
    }
}