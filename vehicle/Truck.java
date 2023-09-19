package vehicle;

import container.Container;
import ports.Ports;

import java.util.ArrayList;
import java.util.List;

public class Truck extends Vehicle {
    public enum TruckType {
        BASIC,
        REEFER,
        TANKER
    }

    private final TruckType truckType;

    public Truck(String name, TruckType truckType, double carryingCapacity, double fuelCapacity, double currentFuel, List<Container> containers) {
        this.name = name;
        this.truckType = truckType;
        this.carryingCapacity = carryingCapacity;
        this.fuelCapacity = fuelCapacity;
        this.currentFuel = currentFuel;
        this.id = generateVehicleId();
        this.containers = new ArrayList<>();

        // Initialize the containerByType map based on the containers list
        for (Container container : containers) {
            containerByType.put(
                    container.getType(),
                    containerByType.getOrDefault(container.getType(), 0) + 1
            );
        }
    }

    @Override
    protected String generateVehicleId() {
        return "tr-" + (++vehicleCounter);
    }

    public boolean addContainer(Container container) {
        // Validate container type based on truck type
        switch (truckType) {
            case BASIC -> {
                if (container.getType() == Container.ContainerType.REFRIGERATED
                        || container.getType() == Container.ContainerType.LIQUID) {
                    return false;
                }
            }
            case REEFER -> {
                if (container.getType() != Container.ContainerType.REFRIGERATED) {
                    return false;
                }
            }
            case TANKER -> {
                if (container.getType() != Container.ContainerType.LIQUID) {
                    return false;
                }
            }
        }

        super.addContainer(container);
        return true;
    }

    @Override
    public boolean canMoveToPort(Ports currentPort, Ports targetPort) {
        if (!targetPort.isLandingAbility()) {
            return false;
        }
        return super.canMoveToPort(currentPort, targetPort);
    }

    public TruckType getTruckType() {
        return truckType;
    }

    @Override
    public String toCSVFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",")
                .append(name).append(",")
                .append(carryingCapacity).append(",")
                .append(fuelCapacity).append(",")
                .append(currentFuel).append(",")
                .append("tr"); // Assuming "tr" is the prefix for truck IDs

        for (Container container : containers) {
            sb.append("\n").append(container.toCSVFormat());
        }

        return sb.toString();
    }
    @Override
    public String toString() {
        return super.toString() + '\n' +
                "Truck Type: " + truckType;
    }
}
