package vehicle;

import ports.Ports;

import java.util.*;
import java.io.*;

public class Truck extends Vehicle {
    public enum TruckType {
        BASIC, REEFER, TANKER
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

    public Truck(String[] data, Ports currentPort) {
        if (data.length < 8) {
            throw new IllegalArgumentException("Insufficient data elements");
        }
        this.id = data[0];
        this.name = data[1];
        this.carryingCapacity = Double.parseDouble(data[2]);
        this.fuelCapacity = Double.parseDouble(data[3]);
        this.currentFuel = Double.parseDouble(data[4]);
        this.currentPort = currentPort;
        try {
            this.truckType = TruckType.valueOf(data[7].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid truck type in data file, setting to default (BASIC).");
            this.truckType = TruckType.BASIC;
        }
    }


    public static Truck createVehicle(Scanner input, Ports currentPortForTruck) {
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
            System.out.print("Please enter the Truck's type (BASIC, TANKER, REEFER): ");
            try {
                truckType = TruckType.valueOf(input.next().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid truck type. Please try again.");
            }
        }
        return new Truck(name, carryingCapacity, fuelCapacity, currentFuel, currentPortForTruck, truckType);
    }

    public static Vehicle addVehicle(Scanner input, List<Ports> portsList) {
        System.out.println("Available ports:");
        for (Ports port : portsList) {
            System.out.println(port.getId());
        }
        System.out.print("Select a port by entering its ID: ");
        String portIdForTruck = input.next();
        Ports currentPortForTruck = findPortById(portsList, portIdForTruck);
        if (currentPortForTruck == null) {
            System.out.println("Invalid choice. Please try again.");
            return null;
        }
        Vehicle newTruck = Truck.createVehicle(input, currentPortForTruck);
        try {
            newTruck.saveToFile("vehicles.csv");
            System.out.println("New truck details saved to vehicles.csv.");
        } catch (IOException e) {
            System.err.println("An error occurred while saving to file: " + e.getMessage());
        }
        return newTruck;
    }

    public static Ports findPortById(List<Ports> portsList, String portId) {
        for (Ports port : portsList) {
            if (port.getId().equals(portId)) {
                return port;
            }
        }
        return null;
    }
}
