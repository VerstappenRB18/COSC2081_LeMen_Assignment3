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
    static final String filename = "ports.txt";


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


    // Method to create and add a new port to the list and save it to the file
    public static void createPort(List<Ports> portsList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter name: ");
        String name = reader.readLine();

        double latitude = -1;
        do {
            System.out.print("Enter latitude (between -90 and 90): ");
            try {
                latitude = Double.parseDouble(reader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (latitude < -90 || latitude > 90);

        double longitude = -1;
        do {
            System.out.print("Enter longitude (between -180 and 180): ");
            try {
                longitude = Double.parseDouble(reader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (longitude < -180 || longitude > 180);

        double storingCapacity = -1;
        do {
            System.out.print("Enter storing capacity (greater than 0): ");
            try {
                storingCapacity = Double.parseDouble(reader.readLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (storingCapacity <= 0);

        Boolean landingAbility = null;
        do {
            System.out.print("Does it have landing ability? (true/false): ");
            String landingAbilityStr = reader.readLine().trim().toLowerCase();
            if (landingAbilityStr.equals("true")) {
                landingAbility = true;
            } else if (landingAbilityStr.equals("false")) {
                landingAbility = false;
            } else {
                System.out.println("Invalid input. Please enter true or false.");
            }
        } while (landingAbility == null);

        Ports port = new Ports(name, latitude, longitude, storingCapacity, landingAbility, null, null, null);
        portsList.add(port);
        port.saveToFile(filename);

        System.out.println("Port created successfully.");
    }

    // Method to update an existing port details
    public static void updatePort(List<Ports> portsList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("Enter port ID to update: ");
            String id = reader.readLine();

            Ports existingPort = null;

            for (Ports port : portsList) {
                if (port.getId().equals(id)) {
                    existingPort = port;
                    break;
                }
            }

            if (existingPort == null) {
                System.err.println("No port found with ID: " + id);
                continue;
            }

            System.out.print("Enter new name: ");
            String name = reader.readLine();

            double latitude = -1;
            do {
                System.out.print("Enter new latitude (between -90 and 90): ");
                try {
                    latitude = Double.parseDouble(reader.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            } while (latitude < -90 || latitude > 90);
            existingPort.setLatitude(latitude);

            double longitude = -1;
            do {
                System.out.print("Enter new longitude (between -180 and 180): ");
                try {
                    longitude = Double.parseDouble(reader.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            } while (longitude < -180 || longitude > 180);
            existingPort.setLongitude(longitude);

            double storingCapacity = -1;
            do {
                System.out.print("Enter new storing capacity (greater than 0): ");
                try {
                    storingCapacity = Double.parseDouble(reader.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            } while (storingCapacity <= 0);
            existingPort.setStoringCapacity(storingCapacity);

            Boolean landingAbility = null;
            do {
                System.out.print("Update landing ability? (true/false): ");
                String landingAbilityStr = reader.readLine().trim().toLowerCase();
                if (landingAbilityStr.equals("true")) {
                    landingAbility = true;
                } else if (landingAbilityStr.equals("false")) {
                    landingAbility = false;
                } else {
                    System.out.println("Invalid input. Please enter true or false.");
                }
            } while (landingAbility == null);
            existingPort.setLandingAbility(landingAbility);

            existingPort.setName(name);

            saveAllToFile(filename, portsList);
            System.out.println("Port updated successfully.");
            return;
        }
    }





    // Method to delete a port from the list and save the updated list to the file
    public static void deletePort(List<Ports> portsList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter port ID to delete: ");
        String id = reader.readLine();

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
