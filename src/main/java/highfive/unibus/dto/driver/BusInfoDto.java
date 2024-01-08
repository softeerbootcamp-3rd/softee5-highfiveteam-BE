package highfive.unibus.dto.driver;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusInfoDto {

    private String busId;
    private String prevStationOrd;

    public BusInfoDto(String busId, String prevStationOrd) {
        this.busId = busId;
        this.prevStationOrd = prevStationOrd;
    }
}
