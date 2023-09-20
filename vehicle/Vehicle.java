package vehicle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import container.Container;
import ports.Ports;
import trip.Trip;

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

    public static int getVehicleCounter() {
        return vehicleCounter;
    }

    public static void setVehicleCounter(int vehicleCounter) {
        Vehicle.vehicleCounter = vehicleCounter;
    }
    private static final double BASE_SHIP_CONSUMPTION = 1.0;
    private static final double BASE_TRUCK_CONSUMPTION = 0.5;
    protected double baseFuelConsumptionRate;

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public double getCurrentFuel() {
        return currentFuel;
    }

    public void setCurrentFuel(double currentFuel) {
        this.currentFuel = currentFuel;
    }

    public static Vehicle createVehicle(Scanner input, List<Ports> portsList) {
        System.out.print("Please enter the Vehicle's name: ");
        String name = input.next();

        System.out.print("Please enter the Vehicle's carrying capacity: ");
        double carryingCapacity = input.nextDouble();

        System.out.print("Please enter the Vehicle's fuel capacity: ");
        double fuelCapacity = input.nextDouble();

        System.out.print("Please enter the Vehicle's current fuel level: ");
        double currentFuel = input.nextDouble();

        // Select a current port
        System.out.println("Available ports:");
        for (int i = 0; i < portsList.size(); i++) {
            System.out.println((i + 1) + ". " + portsList.get(i).getId()); // Assuming Ports class has getId() method
        }
        System.out.print("Select a port by entering its number: ");
        int portIndex = input.nextInt() - 1; // Subtract 1 to convert from 1-based to 0-based index
        Ports currentPort = portsList.get(portIndex);

        // Here we return null because we cannot instantiate an abstract class
        // This method should be overridden in the subclasses (Ship and Truck) to return a new instance of them
        return null;
    }


    public boolean addContainer(Container container) {
        if (container != null) {
            if (this instanceof Truck) {
                Truck thisTruck = (Truck) this;
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
            containers.add(container);
            containerByType.put(
                    container.getType(),
                    containerByType.getOrDefault(container.getType(), 0) + 1
            );
            return true;
        }
        return false;
    }
    public void setCurrentPort(Ports currentPort) {
        this.currentPort = currentPort;
    }

    public Ports getCurrentPort() {
        return currentPort;
    }

    public Map<Container.ContainerType, Integer> getContainerByType() {
        return containerByType;
    }

    public int getTotalContainerCount() {
        return containers.size();
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



    public boolean moveToPort(Ports currentPort, Ports targetPort, String departureDate, String arrivalDate) {
        // First, check if it's possible to move to the target port
        if (!canMoveToPort(currentPort,targetPort)) {
            System.out.println("The vehicle cannot move to the target port due to landing restrictions.");
            return false;
        }

        // Calculate the distance to the target port
        double distanceToTarget = currentPort.calculateDistance(targetPort);

        // Calculate the required fuel for the trip and check if the vehicle has enough fuel
        double requiredFuel = calculateFuelConsumption(distanceToTarget);
        if (requiredFuel > currentFuel) {
            System.out.println("The vehicle does not have enough fuel to complete the trip.");
            return false;
        }

        // Update the vehicle's fuel level
        currentFuel -= requiredFuel;

        // Update the lists of vehicles at the current and target ports
        currentPort.getVehicleList().remove(this);
        targetPort.getVehicleList().add(this);

        // Create a new Trip object to record this journey and add it to the target port's traffic history
        Trip newTrip = new Trip(this, currentPort, targetPort, departureDate, arrivalDate, Trip.TripStatus.COMPLETED);
        targetPort.addTrip(newTrip);

        // Update the vehicle's current location
        this.currentPort = targetPort;

        return true;
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
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",")
                .append(name).append(",")
                .append(carryingCapacity).append(",")
                .append(fuelCapacity).append(",")
                .append(currentFuel).append(",")
                .append(currentPort.getId()).append(",")
                .append(this.getClass().getSimpleName()).append(",") // Add vehicle type
                .append(getContainersCSV()); // Add containers CSV

        return sb.toString();
    }

    String getContainersCSV() {
        StringBuilder sb = new StringBuilder();
        for (Container container : containers) {
            sb.append(container.getId()).append(";");
        }
        // Remove the last semicolon to avoid having an extra semicolon at the end
        if (sb.length() > 0) {
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
        double fuelConsumptionPerKm = getFuelConsumptionRate(containerType.replace("_", " ").toLowerCase());
        return fuelConsumptionPerKm * weight * distance;
    }




    private double getFuelConsumptionRate(String containerType) {
        switch(containerType.toLowerCase()) {
            case "dry storage":
                return this instanceof Ship ? 3.5 : 4.6;
            case "open top":
                return this instanceof Ship ? 2.8 : 3.2;
            case "open side":
                return this instanceof Ship ? 2.7 : 3.2;
            case "refrigerated":
                return this instanceof Ship ? 4.5 : 5.4;
            case "liquid":
                return this instanceof Ship ? 4.8 : 5.3;
            default:
                throw new IllegalArgumentException("Unknown container type: " + containerType);
        }
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
}
