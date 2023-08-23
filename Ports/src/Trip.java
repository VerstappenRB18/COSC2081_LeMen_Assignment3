public class Trip {
    private String vehicleId; // or Vehicle vehicle; if you have a Vehicle class
    private String departurePortId; // or Ports departurePort; if you have a Ports class
    private String arrivalPortId; // or Ports arrivalPort; if you have a Ports class
    private String departureDate;
    private String arrivalDate;
    private String status;


    //default constructor
    public Trip() {
        this.vehicleId = null;
        this.departurePortId = null;
        this.arrivalPortId = null;
        this.departureDate = null;
        this.arrivalDate = null;
        this.status = null;
    }

    // parameterized constructor
    public Trip(String vehicleId, String departurePortId, String arrivalPortId, String departureDate, String arrivalDate, String status) {
        this.vehicleId = vehicleId;
        this.departurePortId = departurePortId;
        this.arrivalPortId = arrivalPortId;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.status = status;
    }
}
