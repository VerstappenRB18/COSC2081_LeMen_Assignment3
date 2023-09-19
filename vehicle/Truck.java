package vehicle;

import java.util.Scanner;
import ports.Ports;

public class Truck extends Vehicle {
    public enum TruckType {
        SMALL, MEDIUM, LARGE
    }

    private TruckType truckType;

    public Truck(String name, double carryingCapacity, double fuelCapacity, double currentFuel, Ports currentPort, TruckType truckType) {
        this.id = generateVehicleId();
        this.name = name;
        this.carryingCapacity = carryingCapacity;
        this.fuelCapacity = fuelCapacity;
        this.currentFuel = currentFuel;
        this.currentPort = currentPort;
        this.truckType = truckType;
    }

    public TruckType getTruckType() {
        return truckType;
    }

    public void setTruckType(TruckType truckType) {
        this.truckType = truckType;
    }

    @Override
    public String toCSVFormat() {
        return id + "," + name + "," + carryingCapacity + "," + fuelCapacity + "," + currentFuel + "," + currentPort.getId() + "," + truckType;
    }

    public static Truck createVehicle(Scanner input, Ports currentPort) {
        System.out.print("Please enter the Truck's name: ");
        String name = input.next();

        System.out.print("Please enter the Truck's carrying capacity: ");
        double carryingCapacity = input.nextDouble();

        System.out.print("Please enter the Truck's fuel capacity: ");
        double fuelCapacity = input.nextDouble();

        System.out.print("Please enter the Truck's current fuel level: ");
        double currentFuel = input.nextDouble();

        TruckType truckType = null;
        while (truckType == null) {
            System.out.print("Please enter the Truck's type (SMALL, MEDIUM, LARGE): ");
            try {
                truckType = TruckType.valueOf(input.next().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid truck type. Please try again.");
            }
        }

        return new Truck(name, carryingCapacity, fuelCapacity, currentFuel, currentPort, truckType);
    }
}
