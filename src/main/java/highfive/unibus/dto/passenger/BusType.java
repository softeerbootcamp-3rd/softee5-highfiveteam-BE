package highfive.unibus.dto.passenger;

public enum BusType {
    간선버스(3),
    지선버스(4),
    순환버스(5);

    private final int length;

    BusType(int length) {
        this.length = length;
    }
}