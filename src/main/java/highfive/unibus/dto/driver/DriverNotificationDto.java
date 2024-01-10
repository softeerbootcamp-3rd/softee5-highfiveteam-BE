package highfive.unibus.dto.driver;

import highfive.unibus.domain.StationPassengerInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DriverNotificationDto {

    private String stationName;
    private int physicalDisabilityNum;
    private int visualDisabilityNum;
    private int getOffNum;

    public DriverNotificationDto(String stationName) {
        this.stationName = stationName;
        this.physicalDisabilityNum = 0;
        this.visualDisabilityNum = 0;
        this.getOffNum = 0;
    }

    public DriverNotificationDto(StationPassengerInfo info, String stationName) {
        this.stationName = stationName;
        this.physicalDisabilityNum = info.getPhysicalDisabilityNum();
        this.visualDisabilityNum = info.getVisualDisabilityNum();
        this.getOffNum = info.getGetOffNum();
    }

    @Override
    public String toString() {
        return "DriverNotificationDto{" +
                "stationName='" + stationName + '\'' +
                ", physicalDisabilityNum=" + physicalDisabilityNum +
                ", visualDisabilityNum=" + visualDisabilityNum +
                ", getOffNum=" + getOffNum +
                '}';
    }
}
