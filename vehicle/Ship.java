package vehicle;

import ports.Ports;

import java.util.*;
import java.io.*;

public class Ship extends Vehicle {
    public Ship(String[] data, Ports currentPort) {
        super();
        this.id = data[0];
        this.name = data[1];
        this.carryingCapacity = Double.parseDouble(data[2]);
        this.fuelCapacity = Double.parseDouble(data[3]);
        this.currentFuel = Double.parseDouble(data[4]);
        this.currentPort = currentPort;
    }


    public Ship(String name, double carryingCapacity, double fuelCapacity, double currentFuel, Ports currentPort) {
        this.id = generateVehicleId();
        this.name = name;
        this.carryingCapacity = carryingCapacity;
        this.fuelCapacity = fuelCapacity;
        this.currentFuel = currentFuel;
        this.currentPort = currentPort;
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



        return new Ship(name, carryingCapacity, fuelCapacity, currentFuel, currentPortForShip);
    }

    public static Vehicle addVehicle(Scanner input, List<Ports> portsList) {
        // Implementation for adding a truck
        System.out.println("Available ports:");
        for (int i = 0; i < portsList.size(); i++) {
            System.out.println((i + 1) + ". " + portsList.get(i).getId());
        }
        System.out.print("Select a port by entering its number: ");
        int portIndexForShip = input.nextInt() - 1;
        if (portIndexForShip < 0 || portIndexForShip >= portsList.size()) {
            System.out.println("Invalid choice. Please try again.");
            return null;
        }
        Ports currentPortForShip = portsList.get(portIndexForShip);
        Vehicle newShip = Ship.createVehicle(input, currentPortForShip, portsList);

        // Save the new truck details to the vehicles.csv file
        try {
            newShip.saveToFile("vehicles.csv");
            System.out.println("New ship details saved to vehicles.csv.");
        } catch (IOException e) {
            System.err.println("An error occurred while saving to file: " + e.getMessage());
        }

        return newShip;
    }

}
