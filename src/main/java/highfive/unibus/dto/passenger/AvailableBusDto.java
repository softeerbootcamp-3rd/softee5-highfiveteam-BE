package highfive.unibus.dto.passenger;

import lombok.Getter;

@Getter
public class AvailableBusDto {

    private String busId;
    private String busNum;
    private String busType;
    private String arrivalTime;
    private String departureOrderInRoute;
    private String destinationOrderInRoute;

    public AvailableBusDto(BusDto departureBusDto, String destinationOrderInRoute) {
        this.busId = departureBusDto.getBusId();
        this.busNum = departureBusDto.getBusNum();
        this.busType = departureBusDto.getBusType();
        this.arrivalTime = departureBusDto.getArrivalTime();
        this.departureOrderInRoute = departureBusDto.getOrderInRoute();
        this.destinationOrderInRoute = destinationOrderInRoute;
    }

}
