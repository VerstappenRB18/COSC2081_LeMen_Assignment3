package trip;

import ports.Ports;
import vehicle.Vehicle;
import javax.sound.sampled.Port;

public class Trip {
    private Vehicle vehicle;
    private Ports departurePort;
    private Ports arrivalPort;
    private String departureDate;
    private String arrivalDate;
    private String status;


    //default constructor
    public Trip() {
        this.vehicle = null;
        this.departurePort = null;
        this.arrivalPort = null;
        this.departureDate = null;
        this.arrivalDate = null;
        this.status = null;
    }

    // parameterized constructor
    public Trip(Vehicle vehicle, Ports departurePort, Ports arrivalPort, String departureDate, String arrivalDate, String status) {
        this.vehicle = vehicle;
        this.departurePort = departurePort;
        this.arrivalPort = arrivalPort;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.status = status;
    }
}
