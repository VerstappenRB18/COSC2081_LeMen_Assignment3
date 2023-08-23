import javax.sound.sampled.Port;

public class Trip {
    private String vehicleId; // or Vehicle vehicle; if you have a Vehicle class
    private Ports departurePort; // or Ports departurePort; if you have a Ports class
    private Ports arrivalPort; // or Ports arrivalPort; if you have a Ports class
    private String departureDate;
    private String arrivalDate;
    private String status;


    //default constructor
    public Trip() {
        this.vehicleId = null;
        this.departurePort = null;
        this.arrivalPort = null;
        this.departureDate = null;
        this.arrivalDate = null;
        this.status = null;
    }

    // parameterized constructor
    public Trip(String vehicleId, Ports departurePort, Ports arrivalPort, String departureDate, String arrivalDate, String status) {
        this.vehicleId = vehicleId;
        this.departurePort = departurePort;
        this.arrivalPort = arrivalPort;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.status = status;
    }
}
