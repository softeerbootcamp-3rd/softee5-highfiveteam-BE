package highfive.unibus.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class StationPassengerInfoId implements Serializable {
    @Column(name = "bus_id")
    private int busId;

    @Column(name = "station_id")
    private int stationId;
}