package hub.domain;

public interface CarPositionWriter {

    public void updateOnce(String carNumber, String lat, String lon);

}
