package vehicle;

import java.util.*;
import container.Container;
import ports.Ports;
import trip.Trip;

public abstract class Vehicle {
    protected static int vehicleCounter = 0;
    protected String id;
    protected String name; // Add this field to store the name of the vehicle
    protected double carryingCapacity;
    protected double fuelCapacity;
    protected double currentFuel;
    protected Ports currentPort;
    protected List<Container> containers = new ArrayList<>();
    protected Map<Container.ContainerType, Integer> containerByType = new HashMap<>();

    protected String generateVehicleId() {
        return "v-" + (++vehicleCounter);
    }
    public abstract String toCSVFormat(); // Add this method signature

    public static int getVehicleCounter() {
        return vehicleCounter;
    }

    public static void setVehicleCounter(int vehicleCounter) {
        Vehicle.vehicleCounter = vehicleCounter;
    }

    public static Vehicle createVehicle(Scanner input, Ports currentPort) {
        System.out.print("Please enter the Vehicle's name: ");
        String name = input.next();

        System.out.print("Please enter the Vehicle's carrying capacity: ");
        double carryingCapacity = input.nextDouble();

        System.out.print("Please enter the Vehicle's fuel capacity: ");
        double fuelCapacity = input.nextDouble();

        System.out.print("Please enter the Vehicle's current fuel level: ");
        double currentFuel = input.nextDouble();

        // Here we return null because we cannot instantiate an abstract class
        // This method should be overridden in the subclasses (Ship and Truck) to return a new instance of them
        return null;
    }

    public boolean addContainer(Container container) {
        if (container != null) {
            containers.add(container);
            containerByType.put(
                    container.getType(),
                    containerByType.getOrDefault(container.getType(), 0) + 1
            );
            return true;
        }
        return false;
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
        for (Container container : containers) {
            if (this instanceof Ship) {
                totalFuelConsumption += container.getFuelConsumptionPerKmForShip() * distance;
            } else if (this instanceof Truck) {
                totalFuelConsumption += container.getFuelConsumptionPerKmForTruck() * distance;
            }
        }
        return totalFuelConsumption;
    }

    public boolean canMoveToPort(Ports currentPort, Ports targetPort) {
        if (!targetPort.isLandingAbility()) {
            return false;
        }

        double distanceToTarget = currentPort.calculateDistance(targetPort);
        double requiredFuel = calculateFuelConsumption(distanceToTarget);

        return requiredFuel <= currentFuel;
    }

    public boolean moveToPort(Ports currentPort, Ports targetPort, String departureDate, String arrivalDate) {
        // First, check if it's possible to move to the target port
        if (!canMoveToPort(currentPort, targetPort)) {
            return false;
        }

        // Calculate the distance to the target port
        double distanceToTarget = currentPort.calculateDistance(targetPort);

        // Calculate the required fuel for the trip and update the vehicle's fuel level
        double requiredFuel = calculateFuelConsumption(distanceToTarget);
        currentFuel -= requiredFuel;

        // Update the lists of vehicles at the current and target ports
        currentPort.getVehicleList().remove(this);
        targetPort.getVehicleList().add(this);

        // Create a new Trip object to record this journey and add it to the target port's traffic history
        Trip newTrip = new Trip();
        targetPort.addTrip(newTrip);

        // Update the vehicle's current location
        this.currentPort = targetPort;

        return true;
    }
    public List<Container> getContainers() {
        return containers;
    }

    public String containersToCSV() {
        StringBuilder sb = new StringBuilder();
        for (Container container : containers) {
            sb.append(container.toCSVFormat()).append("\n");
        }
        return sb.toString();
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
