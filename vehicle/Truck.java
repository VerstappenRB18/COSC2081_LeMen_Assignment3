package vehicle;

import java.util.List;
import java.util.Scanner;
import java.io.*;

import container.Container;
import ports.Ports;

public class Truck extends Vehicle {
    public Truck(String[] data, Ports currentPort) {
        // Validate the length of the data array to prevent ArrayIndexOutOfBoundsException
        if (data.length < 8) {
            throw new IllegalArgumentException("Insufficient data elements");
        }

        // Try to parse truckType, providing a default value in case of invalid data
        this.truckType = TruckType.BASIC; // Set a default value
        try {
            this.truckType = TruckType.valueOf(data[7].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid truck type in data file, setting to default (BASIC).");
        }

        // Validate and parse other fields, providing default values or throwing exceptions as appropriate
        this.id = data[0];
        this.name = data[1];

        try {
            this.carryingCapacity = Double.parseDouble(data[2]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid carrying capacity in data file, setting to default (0.0).");
            this.carryingCapacity = 0.0;
        }

        try {
            this.fuelCapacity = Double.parseDouble(data[3]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid fuel capacity in data file, setting to default (0.0).");
            this.fuelCapacity = 0.0;
        }

        try {
            this.currentFuel = Double.parseDouble(data[4]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid current fuel level in data file, setting to default (0.0).");
            this.currentFuel = 0.0;
        }

        this.currentPort = currentPort;
    }




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


    public String toCSVFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",")
                .append(name).append(",")
                .append(carryingCapacity).append(",")
                .append(fuelCapacity).append(",")
                .append(currentFuel).append(",")
                .append(currentPort.getId()).append(",")
                .append(this.getClass().getSimpleName()).append(",") // Add vehicle type
                .append(truckType.name()).append(",") // Add truck type
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


    public static Truck createVehicle(Scanner input, Ports currentPortForTruck, List<Ports> portsList) {
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
        // Implementation for adding a truck
        System.out.println("Available ports:");
        for (int i = 0; i < portsList.size(); i++) {
            System.out.println((i + 1) + ". " + portsList.get(i).getId());
        }
        System.out.print("Select a port by entering its number: ");
        int portIndexForTruck = input.nextInt() - 1;
        if (portIndexForTruck < 0 || portIndexForTruck >= portsList.size()) {
            System.out.println("Invalid choice. Please try again.");
            return null;
        }
        Ports currentPortForTruck = portsList.get(portIndexForTruck);
        Vehicle newTruck = Truck.createVehicle(input, currentPortForTruck, portsList);

        // Save the new truck details to the vehicles.csv file
        try {
            newTruck.saveToFile("vehicles.csv");
            System.out.println("New truck details saved to vehicles.csv.");
        } catch (IOException e) {
            System.err.println("An error occurred while saving to file: " + e.getMessage());
        }

        return newTruck;
    }
}
