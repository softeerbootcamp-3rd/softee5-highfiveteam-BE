package highfive.unibus.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DriverNotificationDto {

    private String stationName;
    private int physicalDisabilityNum;
    private int visualDisabilityNum;
    private int getOffNum;

}
