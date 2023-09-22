package vehicle;

import ports.Ports;
import static trip.Trip.findVehicleById;

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

    public static void modifyTruckAttributes(List<Vehicle> vehicleList, Scanner scanner) {
        System.out.print("Enter the Truck ID to modify: ");
        String truckId = scanner.next();
        Vehicle vehicle = findVehicleById(vehicleList, truckId);

        if (vehicle instanceof Truck truckToModify) {
            System.out.println("Which attribute would you like to modify?");
            System.out.println("1. Name");
            System.out.println("2. Carrying Capacity");
            System.out.println("3. Fuel Capacity");
            System.out.println("4. Current Fuel");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter new name: ");
                    String newName = scanner.next();
                    truckToModify.setName(newName);
                    break;
                case 2:
                    System.out.print("Enter new carrying capacity: ");
                    double newCarryingCapacity = scanner.nextDouble();
                    truckToModify.setCarryingCapacity(newCarryingCapacity);
                    break;
                case 3:
                    System.out.print("Enter new fuel capacity: ");
                    double newFuelCapacity = scanner.nextDouble();
                    truckToModify.setFuelCapacity(newFuelCapacity);
                    break;
                case 4:
                    double maxFuel = truckToModify.getFuelCapacity();
                    double newCurrentFuel;
                    do {
                        System.out.print("Enter new current fuel (cannot exceed " + maxFuel + "): ");
                        newCurrentFuel = scanner.nextDouble();
                        if (newCurrentFuel > maxFuel) {
                            System.out.println("Current fuel cannot exceed fuel capacity. Please try again.");
                        }
                    } while (newCurrentFuel > maxFuel);
                    truckToModify.setCurrentFuel(newCurrentFuel);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
            List<String> lines = new ArrayList<>();
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.csv"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] fields = line.split(",");
                        if (fields[0].equals(truckId)) {
                            // This is the line for the ship we're modifying
                            line = truckToModify.toCSVFormat();
                        }
                        lines.add(line);
                    }
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.csv"))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("An IO exception occurred: " + e.getMessage());
            }
        } else {
            System.out.println("Truck not found.");
        }
    }

    public static Ports findPortById(List<Ports> portsList, String portId) {
        for (Ports port : portsList) {
            if (port.getId().equals(portId)) {
                return port;
            }
        }
        return null;
    }

    @Override
    public String toCSVFormat() {
        StringBuilder sb = new StringBuilder(super.toCSVFormat()); // Call the parent class's toCSVFormat method
        sb.append(",").append(this.truckType); // Append the truckType

        String containersCSV = super.getContainersCSV();

        if (containersCSV.isEmpty()) {
            sb.append(","); // Add a comma at the end if there are no containers
        } else {
            sb.append(",").append(containersCSV); // Add the containers at the end
        }

        return sb.toString();
    }


}
