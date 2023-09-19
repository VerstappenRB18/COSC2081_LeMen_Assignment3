package vehicle;

import java.util.List;
import java.util.Scanner;

import container.Container;
import ports.Ports;

public class Truck extends Vehicle {
    public Truck(String[] data, Ports currentPort) {
        super();
        this.id = data[0];
        this.name = data[1];
        this.carryingCapacity = Double.parseDouble(data[2]);
        this.fuelCapacity = Double.parseDouble(data[3]);
        this.currentFuel = Double.parseDouble(data[4]);
        this.currentPort = currentPort;
        this.truckType = TruckType.valueOf(data[7].toUpperCase());
        // You will also need to add code here to handle the container data
    }

    private Ports findPortById(List<Ports> portsList, String id) {
        for (Ports port : portsList) {
            if (port.getId().equals(id)) {
                return port;
            }
        }
        return null;
    }


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


    private String getContainersCSV() {
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
