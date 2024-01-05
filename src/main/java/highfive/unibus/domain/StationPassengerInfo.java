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

@Data
@Embeddable
class StationPassengerId implements Serializable {

    @Column(name = "bus_id")
    private int busId;

    @Column(name = "station_id")
    private int stationId;

}
