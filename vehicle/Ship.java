package vehicle;

import container.Container;
import ports.Ports;

public class Ship extends Vehicle{
    public Ship(double carryingCapacity, double fuelCapacity, double currentFuel, Ports currentPort) {
        this.carryingCapacity = carryingCapacity;
        this.fuelCapacity = fuelCapacity;
        this.currentFuel = currentFuel;
        this.currentPort = currentPort;
        this.id = generateVehicleId();
    }

    @Override
    protected String generateVehicleId() {
        return "sh-" + (++vehicleCounter);
    }
}
