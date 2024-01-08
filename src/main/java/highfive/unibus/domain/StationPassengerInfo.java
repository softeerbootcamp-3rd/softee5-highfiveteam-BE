package highfive.unibus.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StationPassengerInfo {

    @EmbeddedId
    private StationPassengerId stationPassengerId;

    private String stationName;

    private int physicalDisabilityNum;

    private int visualDisabilityNum;

    private int getOffNum;

}