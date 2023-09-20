package vehicle;

import ports.Ports;

import java.util.List;
import java.util.Scanner;

public class Ship extends Vehicle {
    public Ship(String[] data, Ports currentPort) {
        super();
        this.id = data[0];
        this.name = data[1];
        this.carryingCapacity = Double.parseDouble(data[2]);
        this.fuelCapacity = Double.parseDouble(data[3]);
        this.currentFuel = Double.parseDouble(data[4]);
        this.currentPort = currentPort;
        this.shipType = ShipType.valueOf(data[7].toUpperCase());

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
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",")
                .append(name).append(",")
                .append(carryingCapacity).append(",")
                .append(fuelCapacity).append(",")
                .append(currentFuel).append(",")
                .append(currentPort.getId()).append(",")
                .append(this.getClass().getSimpleName()).append(",") // Add vehicle type
                .append(shipType.name()).append(",") // Add ship type
                .append(getContainersCSV()); // Add containers CSV

        return sb.toString();
    }



    public static Ship createVehicle(Scanner input, Ports currentPortForShip, List<Ports> portsList) {
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

        return new Ship(name, carryingCapacity, fuelCapacity, currentFuel, currentPortForShip, shipType);
    }



}
