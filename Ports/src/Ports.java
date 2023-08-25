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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getStoringCapacity() {
        return storingCapacity;
    }

    public void setStoringCapacity(double storingCapacity) {
        this.storingCapacity = storingCapacity;
    }

    public boolean isLandingAbility() {
        return landingAbility;
    }

    public void setLandingAbility(boolean landingAbility) {
        this.landingAbility = landingAbility;
    }

    public List<Container> getContainersList() {
        return containersList;
    }

    public void setContainersList(List<Container> containersList) {
        this.containersList = containersList;
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public List<Trip> getTrafficHistory() {
        return trafficHistory;
    }

    public void setTrafficHistory(List<Trip> trafficHistory) {
        this.trafficHistory = trafficHistory;
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
    // Add a vehicle to the vehicle list
    public void addVehicle(Vehicle vehicle) {
        if (vehicle != null) {
            vehicleList.add(vehicle);
        } else {
            System.out.println("Vehicle cannot be null");
        }
    }
    // Add a new trip to the trip list
    public void addTrip(Trip trip) {
        if (trip != null) {
            trafficHistory.add(trip);
        } else {
            System.out.println("Trip cannot be null");
        }
    }

    public double getAvailableStorage() {
        double totalWeight = 0.0;

        // Sum the weight of all containers
        for (Container container : containersList) {
            totalWeight += container.getWeight();
        }

        // Subtract the total weight from the storing capacity

        return storingCapacity - totalWeight;
    }
}
