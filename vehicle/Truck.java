package vehicle;

import container.Container;
import ports.Ports;

public class Truck extends Vehicle {

    public enum TruckType {
        BASIC,
        REEFER,
        TANKER
    }

    private final TruckType truckType;

    public Truck(TruckType truckType, double carryingCapacity, double currentFuel, double fuelCapacity) {
        this.truckType = truckType;
        this.carryingCapacity = carryingCapacity;
        this.fuelCapacity = fuelCapacity;
        this.currentFuel = currentFuel;
        this.id = generateVehicleId();
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
}
