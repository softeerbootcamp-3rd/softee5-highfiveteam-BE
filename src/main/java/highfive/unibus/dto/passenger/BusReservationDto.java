package highfive.unibus.dto.passenger;

import lombok.Getter;

@Getter
public class BusReservationDto {

    private int passengerId;
    private String busId;
    private String disabilityType;
    private String departureStationNum;
    private String destinationStationNum;
    private String departureStationOrd;
    private String destinationStationOrd;

}
