package highfive.unibus.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
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

}