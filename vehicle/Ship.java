package vehicle;

import ports.Ports;
import java.util.Scanner;

public class Ship extends Vehicle {
    public enum ShipType {
        CARGO, CRUISE, CONTAINER
    }

    private ShipType shipType;

    public Ship(String name, double carryingCapacity, double fuelCapacity, double currentFuel, Ports currentPort, ShipType shipType) {
        this.id = generateVehicleId();
        this.name = name;
        this.carryingCapacity = carryingCapacity;
        this.fuelCapacity = fuelCapacity;
        this.currentFuel = currentFuel;
        this.currentPort = currentPort;
        this.shipType = shipType;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    @Override
    public String toCSVFormat() {
        return id + "," + name + "," + carryingCapacity + "," + fuelCapacity + "," + currentFuel + "," + currentPort.getId() + "," + shipType;
    }

    public static Ship createVehicle(Scanner input, Ports currentPort) {
        System.out.print("Please enter the Ship's name: ");
        String name = input.next();

        System.out.print("Please enter the Ship's carrying capacity: ");
        double carryingCapacity = input.nextDouble();

        System.out.print("Please enter the Ship's fuel capacity: ");
        double fuelCapacity = input.nextDouble();

        System.out.print("Please enter the Ship's current fuel level: ");
        double currentFuel = input.nextDouble();

        ShipType shipType = null;
        while (shipType == null) {
            System.out.print("Please enter the Ship's type (CARGO, CRUISE, CONTAINER): ");
            try {
                shipType = ShipType.valueOf(input.next().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid ship type. Please try again.");
            }
        }

        return new Ship(name, carryingCapacity, fuelCapacity, currentFuel, currentPort, shipType);
    }
}
