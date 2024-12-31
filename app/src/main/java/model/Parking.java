package model;

public class Parking {
    private String ParkingId;
    private String ParkingName;
    private String State;

    public Parking(String parkingId, String parkingName, String state) {
        ParkingId = parkingId;
        ParkingName = parkingName;
        State = state;
    }

    public String getParkingId() {
        return ParkingId;
    }

    public void setParkingId(String parkingId) {
        ParkingId = parkingId;
    }

    public String getParkingName() {
        return ParkingName;
    }

    public void setParkingName(String parkingName) {
        ParkingName = parkingName;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}
