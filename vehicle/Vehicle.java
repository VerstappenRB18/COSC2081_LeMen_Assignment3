package vehicle;

import java.io.*;
import java.util.*;
import container.Container;
import ports.Ports;
import trip.Trip;

import static trip.Trip.findVehicleById;
import static vehicle.Main.findPortById;

public abstract class Vehicle {
    protected static int vehicleCounter = 0;
    protected String id;
    protected String name; // Add this field to store the name of the vehicle
    protected double carryingCapacity;
    protected double fuelCapacity;
    protected double currentFuel;
    protected Ports currentPort;
    protected static int shipCounter = 0;
    protected static int truckCounter = 0;
    protected List<Container> containers = new ArrayList<>();
    protected Map<Container.ContainerType, Integer> containerByType = new HashMap<>();

    protected String generateVehicleId() {
        if (this instanceof Ship) {
            return "sh-" + (++shipCounter);
        } else if (this instanceof Truck) {
            return "tr-" + (++truckCounter);
        }
        return "v-" + (++vehicleCounter); // fallback, should not be reached
    }

    public static void setVehicleCounter(int vehicleCounter) {
        Vehicle.vehicleCounter = vehicleCounter;
    }
    private static final double BASE_SHIP_CONSUMPTION = 1.0;
    private static final double BASE_TRUCK_CONSUMPTION = 0.5;

    public static boolean addContainer(Scanner scanner, List<Vehicle> vehicleList, List<Container> containerList) {
        System.out.print("Enter Vehicle ID to add container to: ");
        String vehicleId = scanner.next();
        Vehicle vehicle = findVehicleById(vehicleList, vehicleId);
        if (vehicle == null) {
            System.out.println("Vehicle not found.");
            return false;
        }
        System.out.println("Available Containers:");
        for (int i = 0; i < containerList.size(); i++) {
            System.out.println((i + 1) + ". " + containerList.get(i).toString());
        }
        System.out.print("Enter the ID of the Container to add: ");
        scanner.nextLine();
        String containerId = scanner.nextLine();
        Container container = findContainerById(containerList, containerId);
        if (container != null) {
            if (vehicle instanceof Truck thisTruck) {
                switch (thisTruck.getTruckType()) {
                    case BASIC:
                        if (container.getType() == Container.ContainerType.REFRIGERATED || container.getType() == Container.ContainerType.LIQUID) {
                            System.out.println("This type of truck cannot carry this type of container.");
                            return false;
                        }
                        break;
                    case REEFER:
                        if (container.getType() != Container.ContainerType.REFRIGERATED) {
                            System.out.println("This type of truck can only carry refrigerated containers.");
                            return false;
                        }
                        break;
                    case TANKER:
                        if (container.getType() != Container.ContainerType.LIQUID) {
                            System.out.println("This type of truck can only carry liquid containers.");
                            return false;
                        }
                        break;
                }
            }
            vehicle.getContainers().add(container);
            vehicle.getContainerByType().put(
                    container.getType(),
                    vehicle.getContainerByType().getOrDefault(container.getType(), 0) + 1
            );
            System.out.println("Container added to vehicle.");
            return true;
        } else {
            System.out.println("Invalid container ID. Please try again.");
            return false;
        }
    }


    public Ports getCurrentPort() {
        return currentPort;
    }

    public Map<Container.ContainerType, Integer> getContainerByType() {
        return containerByType;
    }


    public boolean unloadContainer(Container container, Ports port) {
        if (containers.remove(container)) {
            port.getContainersList().add(container);
            containerByType.put(
                    container.getType(),
                    containerByType.getOrDefault(container.getType(), 1) - 1
            );
            return true;
        }
        return false;
    }

    public double calculateFuelConsumption(double distance) {
        double totalFuelConsumption = 0.0;

        // Base fuel consumption rate for the vehicle when it is empty
        double baseFuelConsumptionRate;
        if (this instanceof Ship) {
            baseFuelConsumptionRate = 1.0; // Set a value appropriate for a ship
        } else { // this instanceof Truck
            baseFuelConsumptionRate = 0.5; // Set a value appropriate for a truck
        }

        // Calculate the base fuel consumption
        totalFuelConsumption += baseFuelConsumptionRate * distance;

        // Add the fuel consumption due to the containers (if any)
        for (Container container : containers) {
            if (this instanceof Ship) {
                totalFuelConsumption += container.getFuelConsumptionPerKmForShip() * distance;
            } else { // this instanceof Truck
                totalFuelConsumption += container.getFuelConsumptionPerKmForTruck() * distance;
            }
        }

        return totalFuelConsumption;
    }


    public boolean hasEnoughFuel(double distance) {
        double requiredFuel = calculateFuelConsumption(distance);
        return requiredFuel <= currentFuel;
    }


    public boolean canMoveToPort(Ports currentPort, Ports targetPort) {
        if (!targetPort.isLandingAbility()) {
            return false;
        }

        double distanceToTarget = currentPort.calculateDistance(targetPort);
        return hasEnoughFuel(distanceToTarget);
    }


    public List<Container> getContainers() {
        return containers;
    }
    public void saveToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(toCSVFormat() + "\n");
        }
    }


    public String toCSVFormat() {

        return id + "," +
                name + "," +
                carryingCapacity + "," +
                fuelCapacity + "," +
                currentFuel + "," +
                currentPort.getId() + "," +
                this.getClass().getSimpleName() + "," + // Add vehicle type
                getContainersCSV();
    }

    String getContainersCSV() {
        StringBuilder sb = new StringBuilder();
        for (Container container : containers) {
            sb.append(container.getId()).append(";");
        }
        // Remove the last semicolon to avoid having an extra semicolon at the end
        if (!sb.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }


    public double calculateDailyFuelConsumption(double dailyDistance) {
        double baseFuelConsumptionRate = this instanceof Ship ? BASE_SHIP_CONSUMPTION : BASE_TRUCK_CONSUMPTION;
        double totalFuelConsumption = baseFuelConsumptionRate * dailyDistance; // Base consumption for the distance

        for (Container container : containers) {
            double additionalConsumption = calculateFuelConsumption(dailyDistance, container.getWeight(), container.getType().name());
            totalFuelConsumption += additionalConsumption - (baseFuelConsumptionRate * dailyDistance); // Subtract the base consumption to avoid double counting
        }

        return totalFuelConsumption;
    }



    public double calculateFuelConsumption(double distance, double weight, String containerType) {
        double fuelConsumptionPerKm = getFuelConsumptionRate(containerType.replace("_", " ").toUpperCase());
        return fuelConsumptionPerKm * weight * distance;
    }


    private double getFuelConsumptionRate(String containerType) {
        return switch (containerType.toUpperCase()) {
            case "DRY_STORAGE" -> this instanceof Ship ? 3.5 : 4.6;
            case "OPEN_TOP" -> this instanceof Ship ? 2.8 : 3.2;
            case "OPEN_SIDE" -> this instanceof Ship ? 2.7 : 3.2;
            case "REFRIGERATED" -> this instanceof Ship ? 4.5 : 5.4;
            case "LIQUID" -> this instanceof Ship ? 4.8 : 5.3;
            default -> throw new IllegalArgumentException("Unknown container type: " + containerType);
        };
    }

    public static void refuel(Scanner input, List<Vehicle> vehicleList) {
        System.out.print("Enter Vehicle ID to refuel: ");
        String vehicleId = input.next();

        Vehicle vehicle = findVehicleById(vehicleList, vehicleId);
        if (vehicle != null) {
            vehicle.currentFuel = vehicle.fuelCapacity;
            System.out.println("Vehicle successfully refueled to maximum capacity. Current fuel level: " + vehicle.currentFuel + " gallons.");
        } else {
            System.out.println("No vehicle found with the ID: " + vehicleId);
        }
    }

    public String getId() {
        return id;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vehicle ID: ").append(id).append('\n')
                .append("Name: ").append(name).append('\n')
                .append("Carrying Capacity: ").append(carryingCapacity).append('\n')
                .append("Fuel Capacity: ").append(fuelCapacity).append('\n')
                .append("Current Fuel: ").append(currentFuel).append('\n');

        if (this instanceof Truck) {
            sb.append("Truck Type: ").append(((Truck) this).getTruckType()).append('\n');
        }

        sb.append("Containers: ");
        for (Container container : containers) {
            sb.append(container.toString()).append(", ");
        }

        return sb.toString();
    }

    private static Container findContainerById(List<Container> containerList, String id) {
        for (Container container : containerList) {
            if (container.getId().equals(id)) {
                return container;
            }
        }
        return null;
    }

    public static void handleUnloadContainer(Scanner scanner, List<Vehicle> vehicleList, String filePath) {
        System.out.print("Enter Vehicle ID to unload a container from: ");
        String unloadVehicleId = scanner.next();
        Vehicle unloadVehicle = findVehicleById(vehicleList, unloadVehicleId);
        if (unloadVehicle == null) {
            System.out.println("Vehicle not found.");
            return;
        }

        if (unloadVehicle.getContainers().isEmpty()) {
            System.out.println("No container to unload.");
            return;
        }

        Container unloadContainer = unloadVehicle.getContainers().get(0);
        if (unloadVehicle.unloadContainer(unloadContainer, unloadVehicle.getCurrentPort())) {
            System.out.println("Container unloaded successfully.");

            // Save the updated data to the file
            try {
                saveAllData(vehicleList, filePath);
            } catch (IOException e) {
                System.err.println("Error saving data: " + e.getMessage());
            }
        } else {
            System.out.println("Failed to unload container.");
        }
    }

    public static void saveAllData(List<Vehicle> vehicleList, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Vehicle vehicle : vehicleList) {
                if (vehicle != null) {
                    writer.write(vehicle.toCSVFormat());
                    writer.newLine();
                }
            }
        }
    }

    public static List<Vehicle> loadVehiclesFromFile(String filePath, List<Ports> portsList) throws IOException {
        List<Vehicle> vehicleList = new ArrayList<>();
        int maxVehicleId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Vehicle vehicle;
                Ports currentPort = findPortById(portsList, data[5]);
                if ("Truck".equals(data[6])) {
                    vehicle = new Truck(data, currentPort);
                } else {
                    vehicle = new Ship(data, currentPort);
                }
                vehicleList.add(vehicle);

                // Extract the vehicle ID number and find the maximum ID number
                int vehicleIdNumber = Integer.parseInt(data[0].substring(2));
                maxVehicleId = Math.max(maxVehicleId, vehicleIdNumber);
            }
        }

        // Set the vehicleCounter in the Vehicle class to the highest ID number found
        Vehicle.setVehicleCounter(maxVehicleId);

        return vehicleList;
    }
}
