import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Ports {
    String id;
    String name;
    double latitude;
    double longitude;
    double storingCapacity;
    boolean landingAbility;
    List<Container> containersList;
    List<Vehicle> vehicleList;
    List<Trip>trafficHistory;

    // Default Constructor
    public Ports() {
        this.id = null;
        this.name = null;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.storingCapacity = 0.0;
        this.landingAbility = false;
        this.containersList = null ;
        this.vehicleList = null;
        this.trafficHistory = null;
    }

    // Parameter Constructor
    public Ports(String id, String name, double latitude, double longitude, double storingCapacity, boolean landingAbility, List<Container> containersList, List<Vehicle> vehicleList, List<Trip> trafficHistory) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.storingCapacity = storingCapacity;
        this.landingAbility = landingAbility;
        this.containersList = containersList != null ? containersList : new ArrayList<>();
        this.vehicleList = vehicleList != null ? vehicleList : new ArrayList<>();
        this.trafficHistory = trafficHistory != null ? trafficHistory : new ArrayList<>();
    }


    // calculateDistance
    public double calculateDistance(Ports otherPort) {
        final int R = 6371; // Radius of the Earth in kilometers

        double latDistance = Math.toRadians(otherPort.latitude - this.latitude);
        double lonDistance = Math.toRadians(otherPort.longitude - this.longitude);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.latitude)) * Math.cos(Math.toRadians(otherPort.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // The distance in kilometers
    }

    // Add a container to the containers list
    public void addContainer(Container container) {
        if (container != null) {
            containersList.add(container);
        } else {
            System.out.println("Container cannot be null");
        }
    }
}
