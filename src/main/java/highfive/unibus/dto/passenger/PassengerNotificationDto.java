package highfive.unibus.dto.passenger;

import lombok.Builder;

@Builder
public class PassengerNotificationDto {

    private String stationName;
    private String vehicleNum;

    @Override
    public String toString() {
        return "PassengerNotificationDto{" +
                "stationName='" + stationName + '\'' +
                ", vehicleNum='" + vehicleNum + '\'' +
                '}';
    }
}
