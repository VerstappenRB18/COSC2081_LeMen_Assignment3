package vehicle;

import java.io.*;
import java.util.*;
import container.Container;
import ports.Ports;
import trip.Trip;

public class Ship extends Vehicle {
    public Ship(String name, double carryingCapacity, double fuelCapacity, double currentFuel, Ports currentPort) {
        this.name = name;
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

    @Override
    public String toCSVFormat() {
        return String.join(",",
                this.getId(),
                this.name,
                String.valueOf(this.carryingCapacity),
                String.valueOf(this.currentFuel),
                String.valueOf(this.fuelCapacity),
                String.valueOf(this.containers.size())
        );
    }
    @Override
    public String toString() {
        return super.toString() + '\n' +
                "Current Port: " + (currentPort != null ? currentPort.getName() : "N/A");
    }
}
