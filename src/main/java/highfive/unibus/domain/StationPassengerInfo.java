package highfive.unibus.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StationPassengerInfo {

    @EmbeddedId
    private StationPassengerInfoId stationPassengerInfoId;

    private String stationName;

    private int physicalDisabilityNum;

    private int visualDisabilityNum;

    private int getOffNum;

    @Override
    public String toString() {
        return "StationPassengerInfo{" +
                "stationPassengerInfoId=" + stationPassengerInfoId +
                ", stationName='" + stationName + '\'' +
                ", physicalDisabilityNum=" + physicalDisabilityNum +
                ", visualDisabilityNum=" + visualDisabilityNum +
                ", getOffNum=" + getOffNum +
                '}';
    }
}