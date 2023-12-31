package vehicle;

import java.io.*;
import java.util.*;
import container.Container;
import ports.Ports;


import static trip.Trip.findVehicleById;

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

    public static void updateVehicleCounters(List<Vehicle> vehicleList) {
        int maxShipId = 0;
        int maxTruckId = 0;

        for (Vehicle vehicle : vehicleList) {
            String vehicleId = vehicle.getId();
            if (vehicle instanceof Ship) {
                int shipIdNumber = Integer.parseInt(vehicleId.replace("sh-", ""));
                if (shipIdNumber > maxShipId) {
                    maxShipId = shipIdNumber;
                }
            } else if (vehicle instanceof Truck) {
                int truckIdNumber = Integer.parseInt(vehicleId.replace("tr-", ""));
                if (truckIdNumber > maxTruckId) {
                    maxTruckId = truckIdNumber;
                }
            }
        }

        shipCounter = maxShipId;
        truckCounter = maxTruckId;
    }

    public static void setVehicleCounter(int vehicleCounter) {
        Vehicle.vehicleCounter = vehicleCounter;
    }
    private static final double BASE_SHIP_CONSUMPTION = 1.0;
    private static final double BASE_TRUCK_CONSUMPTION = 0.5;


    public double getCarryingCapacity() {
        return carryingCapacity;
    }

    public static void addContainer(Scanner scanner, List<Vehicle> vehicleList, List<Container> containerList) {
        System.out.print("Enter Vehicle ID to add container to: ");
        String vehicleId = scanner.next();
        Vehicle vehicle = findVehicleById(vehicleList, vehicleId);
        if (vehicle == null) {
            System.out.println("Vehicle not found.");
            return;
        }

        // Automatically filter containers based on the current port of the vehicle
        List<Container> availableContainers = new ArrayList<>();
        for (Container container : containerList) {
            if (container.getPortId().equals(vehicle.getCurrentPort().getId())) {
                availableContainers.add(container);
            }
        }

        if (availableContainers.isEmpty()) {
            System.out.println("No containers available at the current port.");
            return;
        }

        System.out.println("Available Containers:");
        for (int i = 0; i < availableContainers.size(); i++) {
            System.out.println((i + 1) + ". " + availableContainers.get(i).toString());
        }

        System.out.print("Enter the ID of the Container to add: ");
        scanner.nextLine();
        String containerId = scanner.nextLine();
        Container container = findContainerById(availableContainers, containerId); // Note: We're searching in availableContainers
        if (container == null) {
            System.out.println("Invalid container ID. Please try again.");
            return;
        }

        // Existing check for container type compatibility with the vehicle
        if (vehicle instanceof Truck thisTruck) {
            switch (thisTruck.getTruckType()) {
                case BASIC:
                    if (container.getType() == Container.ContainerType.REFRIGERATED || container.getType() == Container.ContainerType.LIQUID) {
                        System.out.println("This type of truck cannot carry this type of container.");
                        return;
                    }
                    break;
                case REEFER:
                    if (container.getType() != Container.ContainerType.REFRIGERATED) {
                        System.out.println("This type of truck can only carry refrigerated containers.");
                        return;
                    }
                    break;
                case TANKER:
                    if (container.getType() != Container.ContainerType.LIQUID) {
                        System.out.println("This type of truck can only carry liquid containers.");
                        return;
                    }
                    break;
            }
        }

        // Check if adding the container will exceed the vehicle's weight capacity
        double totalWeight = vehicle.getContainers().stream().mapToDouble(Container::getWeight).sum();
        if (totalWeight + container.getWeight() > vehicle.getCarryingCapacity()) {
            System.out.println("Adding this container will exceed the vehicle's carrying capacity.");
            return;
        }

        // If all checks pass, add the container to the vehicle
        vehicle.getContainers().add(container);
        vehicle.getContainerByType().put(
                container.getType(),
                vehicle.getContainerByType().getOrDefault(container.getType(), 0) + 1
        );
        System.out.println("Container added to vehicle.");
        System.out.println("Added container " + container.getId() + " to vehicle " + vehicle.getId());
    }


    public Ports getCurrentPort() {
        return currentPort;
    }

    public void setCurrentPort(Ports currentPort) {
        this.currentPort = currentPort;
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
                (currentPort != null ? currentPort.getId() : "null") + "," + // Include currentPort
                this.getClass().getSimpleName();
    }


    public String getContainersCSV() {
        StringBuilder sb = new StringBuilder();
        for (Container container : containers) {
            sb.append(container.getId()).append(";");
        }
        if (!containers.isEmpty()) {
            sb.setLength(sb.length() - 1); // Remove the last semicolon
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

    public static void updateVehicleFuelInCSV(Vehicle vehicle, String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(vehicle.getId())) {
                    data[4] = String.valueOf(vehicle.getCurrentFuel());
                    line = String.join(",", data);
                }
                lines.add(line);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                // Only add a new line if this is not the last line
                if (i < lines.size() - 1) {
                    writer.newLine();
                }
            }
        }
    }



    public double calculateFuelConsumption(double distance, double weight, String containerType) {
        double fuelConsumptionPerKm = getFuelConsumptionRate(containerType.replace("_", " ").toLowerCase());
        return fuelConsumptionPerKm * weight * distance;
    }




    private double getFuelConsumptionRate(String containerType) {
        return switch (containerType.toLowerCase()) {
            case "dry storage" -> this instanceof Ship ? 3.5 : 4.6;
            case "open top" -> this instanceof Ship ? 2.8 : 3.2;
            case "open side" -> this instanceof Ship ? 2.7 : 3.2;
            case "refrigerated" -> this instanceof Ship ? 4.5 : 5.4;
            case "liquid" -> this instanceof Ship ? 4.8 : 5.3;
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
        sb.append("Vehicle Information:").append('\n')
                .append("  - ID: ").append(id).append('\n')
                .append("  - Name: ").append(name).append('\n')
                .append("  - Carrying Capacity: ").append(carryingCapacity).append('\n')
                .append("  - Fuel Capacity: ").append(fuelCapacity).append('\n')
                .append("  - Current Fuel: ").append(currentFuel).append('\n');

        if (this instanceof Truck) {
            sb.append("  - Truck Type: ").append(((Truck) this).getTruckType()).append('\n');
        }

        if (containers.isEmpty()) {
            sb.append("  - Containers: None");
        } else {
            sb.append("  - Containers:");
            for (Container container : containers) {
                sb.append("\n    * ").append(container.toString());
            }
        }

        return sb.toString();
    }


    public static Container findContainerById(List<Container> containerList, String id) {
        for (Container container : containerList) {
            if (container.getId().equals(id)) {
                return container;
            }
        }
        return null;
    }

    public static Vehicle findVehicleByContainerId(List<Vehicle> vehicleList, String containerId) {
        for (Vehicle v : vehicleList) {
            for (Container c : v.getContainers()) {
                if (c.getId().equals(containerId)) {
                    return v;
                }
            }
        }
        return null;
    }


    public static void deleteVehicle(List<Vehicle> vehicleList, Scanner scanner, String filename) throws IOException {
        // Prompt the user for the vehicle ID
        System.out.print("Enter the Vehicle ID to delete: ");
        String vehicleId = scanner.next();

        // Find the vehicle by its ID
        Vehicle vehicleToDelete = null;
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getId().equals(vehicleId)) {
                vehicleToDelete = vehicle;
                break;
            }
        }

        // If the vehicle is found, remove it from the list
        if (vehicleToDelete != null) {
            vehicleList.remove(vehicleToDelete);

            // Write the updated list back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (Vehicle vehicle : vehicleList) {
                    writer.write(vehicle.toCSVFormat() + "\n");
                }
            }

            System.out.println("Vehicle deleted successfully.");
        } else {
            System.out.println("Vehicle not found.");
        }
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public double getCurrentFuel() {
        return currentFuel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCarryingCapacity(double carryingCapacity) {
        this.carryingCapacity = carryingCapacity;
    }

    public void setFuelCapacity(double fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public void setCurrentFuel(double currentFuel) {
        this.currentFuel = currentFuel;
    }
}
