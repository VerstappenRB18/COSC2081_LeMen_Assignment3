package ports;

import container.Container;
import trip.Trip;
import vehicle.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ports {
    private static int portCounter = 0;
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private double storingCapacity;
    private boolean landingAbility;
    private List<Container> containersList;
    private List<Vehicle> vehicleList;
    private List<Trip> trafficHistory;
    private static final String filename = "ports.txt";


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
    public Ports(String name, double latitude, double longitude, double storingCapacity, boolean landingAbility, List<Container> containersList, List<Vehicle> vehicleList, List<Trip> trafficHistory) throws IOException {
        this.id = generatePortId();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.storingCapacity = storingCapacity;
        this.landingAbility = landingAbility;
        this.containersList = containersList != null ? containersList : new ArrayList<>();
        this.vehicleList = vehicleList != null ? vehicleList : new ArrayList<>();
        this.trafficHistory = trafficHistory != null ? trafficHistory : new ArrayList<>();
    }


    private String generatePortId() throws IOException {
        Set<String> existingIds = getExistingIds();
        String newId;
        do {
            newId = "p-" + (++portCounter);
        } while (existingIds.contains(newId));

        return newId;
    }

    private Set<String> getExistingIds() {
        Set<String> existingIds = new HashSet<>();
        File file = new File(filename);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] details = line.split(",");
                    existingIds.add(details[0]);
                }
            } catch (IOException e) {
                System.err.println("Error reading the file while generating port ID: " + e.getMessage());
            }
        }

        return existingIds;
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


    // CRUD operation methods
    public void saveToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(String.format("%s,%s,%.2f,%.2f,%.2f,%b%n", id, name, latitude, longitude, storingCapacity, landingAbility));
        }
    }

    public static List<Ports> readFromFile(String filename) throws IOException {
        List<Ports> portsList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                Ports port = new Ports();
                port.setId(details[0]);
                port.setName(details[1]);
                port.setLatitude(Double.parseDouble(details[2]));
                port.setLongitude(Double.parseDouble(details[3]));
                port.setStoringCapacity(Double.parseDouble(details[4]));
                port.setLandingAbility(Boolean.parseBoolean(details[5]));
                portsList.add(port);
            }
        }
        System.out.println("Read from file: " + portsList.size() + " ports.");
        for(Ports port : portsList) {
            System.out.println(port.toString());
        }
        return portsList;
    }


    public static void updatePort(String filename, String id, Ports updatedPort, List<Ports> portsList) throws IOException {
        for (int i = 0; i < portsList.size(); i++) {
            if (portsList.get(i).getId().equals(id)) {
                Ports existingPort = portsList.get(i);
                existingPort.setName(updatedPort.getName());
                existingPort.setLatitude(updatedPort.getLatitude());
                existingPort.setLongitude(updatedPort.getLongitude());
                existingPort.setStoringCapacity(updatedPort.getStoringCapacity());
                existingPort.setLandingAbility(updatedPort.isLandingAbility());
                existingPort.setContainersList(updatedPort.getContainersList());
                existingPort.setVehicleList(updatedPort.getVehicleList());
                existingPort.setTrafficHistory(updatedPort.getTrafficHistory());

                saveAllToFile(filename, portsList);
                System.out.println("Port updated successfully.");
                return;
            }
        }
        System.err.println("No port found with ID: " + id);
    }

    public static void deletePort(String filename, String id, List<Ports> portsList) throws IOException {
        for (int i = 0; i < portsList.size(); i++) {
            if (portsList.get(i).getId().equals(id)) {
                portsList.remove(i);
                saveAllToFile(filename, portsList);
                System.out.println("Port deleted successfully.");
                return;
            }
        }
        System.err.println("No port found with ID: " + id);
    }


    public static void saveAllToFile(String filename, List<Ports> portsList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Ports port : portsList) {
                writer.write(String.format("%s,%s,%.2f,%.2f,%.2f,%b%n", port.getId(), port.getName(), port.getLatitude(), port.getLongitude(), port.getStoringCapacity(), port.isLandingAbility()));
                // You'll want to extend this to include the other attributes of a Port
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Port ID: %s, Name: %s, Latitude: %.2f, Longitude: %.2f, Storing Capacity: %.2f, Landing Ability: %b",
                id, name, latitude, longitude, storingCapacity, landingAbility);
    }

}
