package ports;

import User.User;
import container.Container;
import trip.Trip;
import vehicle.Ship;
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
    private final List<Container> containersList;
    private final List<Vehicle> vehicleList;
    private final List<Trip> trafficHistory;
    static final String filename = "ports.csv";


    // Default Constructor
    public Ports() {
        this.id = null;
        this.name = null;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.storingCapacity = 0.0;
        this.landingAbility = false;
        this.containersList = new ArrayList<>(); // Initialize to an empty list
        this.vehicleList = new ArrayList<>(); // Initialize to an empty list
        this.trafficHistory = new ArrayList<>(); // Initialize to an empty list
    }

    // Parameter Constructor
    public Ports(String name, double latitude, double longitude, double storingCapacity, boolean landingAbility, List<Container> containersList, List<Vehicle> vehicleList, List<Trip> trafficHistory) {
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


    private String generatePortId() {
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


    // CRUD operation methods
    public void saveToFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        lines.add(String.format("%s,%s,%.2f,%.2f,%.2f,%b", id, name, latitude, longitude, storingCapacity, landingAbility));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) {
                    writer.newLine();
                }
            }
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

    public static void displayAccessiblePorts(User loggedInUser) {
        try {
            List<Ports> allPorts = Ports.readFromFile("ports.csv");
            List<Ports> accessiblePorts = Ports.filterPortsByAccess(allPorts, loggedInUser.getPortId(), loggedInUser.getUserRole());

            if (accessiblePorts.isEmpty()) {
                System.out.println("No accessible ports found.");
                return;
            }

            for (Ports port : accessiblePorts) {
                System.out.println(port);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the ports file: " + e.getMessage());
        }
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

            System.out.print("Enter new name (press enter to keep current): ");
            String name = reader.readLine();
            if (!name.isBlank()) {
                existingPort.setName(name);
            }

            double latitude = getDoubleInput(reader, "Enter new latitude (between -90 and 90, press enter to keep current): ", existingPort.getLatitude(), -90, 90);
            existingPort.setLatitude(latitude);

            double longitude = getDoubleInput(reader, "Enter new longitude (between -180 and 180, press enter to keep current): ", existingPort.getLongitude(), -180, 180);
            existingPort.setLongitude(longitude);

            double storingCapacity = getDoubleInput(reader, "Enter new storing capacity (greater than 0, press enter to keep current): ", existingPort.getStoringCapacity(), 0, Double.MAX_VALUE);
            existingPort.setStoringCapacity(storingCapacity);

            System.out.print("Update landing ability? (true/false, press enter to keep current): ");
            String landingAbilityStr;
            while (true) {
                landingAbilityStr = reader.readLine().trim().toLowerCase();
                if (landingAbilityStr.isBlank()) {
                    break;
                } else if (landingAbilityStr.equals("true") || landingAbilityStr.equals("false")) {
                    existingPort.setLandingAbility(Boolean.parseBoolean(landingAbilityStr));
                    break;
                } else {
                    System.out.println("Invalid input. Please enter true, false, or press enter to keep the current value.");
                }
            }

            saveAllToFile(filename, portsList);
            System.out.println("Port updated successfully.");
            return;
        }
    }

    private static double getDoubleInput(BufferedReader reader, String prompt, double currentValue, double minValue, double maxValue) throws IOException {
        Double newValue = null;
        do {
            System.out.print(prompt);
            String input = reader.readLine();
            if (input.isBlank()) {
                return currentValue;
            }
            try {
                newValue = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        } while (newValue == null || newValue < minValue || newValue > maxValue);
        return newValue;
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

    public static void listAllShips(List<Ports> portsList, List<Vehicle> vehicleList) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Please enter the ID of the port: ");
        String portId;
        try {
            portId = reader.readLine();
        } catch (IOException e) {
            System.err.println("An error occurred while reading the input: " + e.getMessage());
            return;
        }

        Ports selectedPort = null;
        for (Ports port : portsList) {
            if (port.getId().equals(portId)) {
                selectedPort = port;
                break;
            }
        }

        if (selectedPort == null) {
            System.out.println("No port found with the ID: " + portId);
            return;
        }

        System.out.println("Listing all ships at the port:");
        boolean found = false;
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getCurrentPort() != null && vehicle.getCurrentPort().getId().equals(portId) && vehicle instanceof Ship) {
                System.out.println(vehicle);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No ships found at this port.");
        }
    }

    public static List<Ports> filterPortsByAccess(List<Ports> allPorts, String portId, User.UserRole userRole) {
        List<Ports> accessiblePorts = new ArrayList<>();

        if (userRole == User.UserRole.ADMIN) {
            return allPorts; // Admin has access to all ports
        }

        for (Ports port : allPorts) {
            if (port.getId().equals(portId)) {
                accessiblePorts.add(port);
            }
        }

        return accessiblePorts; // Manager has access to specific port
    }



    public static void saveAllToFile(String filename, List<Ports> portsList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < portsList.size(); i++) {
                Ports port = portsList.get(i);
                writer.write(String.format("%s,%s,%.2f,%.2f,%.2f,%b", port.getId(), port.getName(), port.getLatitude(), port.getLongitude(), port.getStoringCapacity(), port.isLandingAbility()));
                if (i < portsList.size() - 1) {
                    writer.newLine();
                }
            }
        }
    }



    @Override
    public String toString() {
        return "Port Information:" + "\n" +
                "  - ID: " + id + "\n" +
                "  - Name: " + name + "\n" +
                "  - Coordinates: (" + latitude + ", " + longitude + ")\n" +
                "  - Storing Capacity: " + storingCapacity + "\n" +
                "  - Landing Ability: " + landingAbility;
    }


    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }


}
