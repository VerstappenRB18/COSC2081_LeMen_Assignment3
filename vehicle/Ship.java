package vehicle;

import ports.Ports;
import User.User;

import java.util.*;
import java.io.*;
import static trip.Trip.findVehicleById;

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

    public static Ship createVehicle(Scanner input, Ports currentPortForShip) {
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
        System.out.println("Available ports:");
        for (Ports port : portsList) {
            System.out.println(port.getId());
        }
        System.out.print("Select a port by entering its ID: ");
        String portIdForShip = input.next();
        Ports currentPortForShip = findPortById(portsList, portIdForShip);
        if (currentPortForShip == null) {
            System.out.println("Invalid choice. Please try again.");
            return null;
        }
        Vehicle newShip = Ship.createVehicle(input, currentPortForShip);
        try {
            newShip.saveToFile("vehicles.csv");
            System.out.println("New ship details saved to vehicles.csv.");
        } catch (IOException e) {
            System.err.println("An error occurred while saving to file: " + e.getMessage());
        }
        return newShip;
    }

    public static Ports findPortById(List<Ports> portsList, String portId) {
        for (Ports port : portsList) {
            if (port.getId().equals(portId)) {
                return port;
            }
        }
        return null;
    }

    public static void modifyShipAttributes(List<Vehicle> vehicleList, Scanner scanner, User loggedInUser) {
        List<String> modifiableShips = new ArrayList<>();

        for (Vehicle vehicle : vehicleList) {
            if (vehicle instanceof Ship) {
                if (loggedInUser.getUserRole() == User.UserRole.ADMIN ||
                        vehicle.getCurrentPort().getId().equals(loggedInUser.getPortId())) {
                    modifiableShips.add(vehicle.getId());
                }
            }
        }

        System.out.println("Ships you can modify: " + String.join(", ", modifiableShips));
        System.out.println();
        System.out.print("Enter the Ship ID to modify: ");
        String shipId = scanner.next();
        Vehicle vehicle = findVehicleById(vehicleList, shipId);

        if (vehicle instanceof Ship shipToModify) {
            if (loggedInUser.getUserRole() == User.UserRole.MANAGER &&
                    !shipToModify.getCurrentPort().getId().equals(loggedInUser.getPortId())) {
                System.out.println("You are not authorized to modify ships that don't belong to your port.");
                return;
            }
            System.out.println("1. Name");
            System.out.println("2. Carrying Capacity");
            System.out.println("3. Fuel Capacity");
            System.out.println("4. Current Fuel");
            System.out.print("Which attribute would you like to modify?: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter new name: ");
                    String newName = scanner.next();
                    shipToModify.setName(newName);
                    break;
                case 2:
                    System.out.print("Enter new carrying capacity: ");
                    double newCarryingCapacity = scanner.nextDouble();
                    shipToModify.setCarryingCapacity(newCarryingCapacity);
                    break;
                case 3:
                    System.out.print("Enter new fuel capacity: ");
                    double newFuelCapacity = scanner.nextDouble();
                    shipToModify.setFuelCapacity(newFuelCapacity);
                    break;
                case 4:
                    double maxFuel = shipToModify.getFuelCapacity();
                    double newCurrentFuel;
                    do {
                        System.out.print("Enter new current fuel (cannot exceed " + maxFuel + "): ");
                        newCurrentFuel = scanner.nextDouble();
                        if (newCurrentFuel > maxFuel) {
                            System.out.println("Current fuel cannot exceed fuel capacity. Please try again.");
                        }
                    } while (newCurrentFuel > maxFuel);
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
                        if (fields[0].equals(shipId)) {
                            // This is the line for the ship we're modifying
                            line = shipToModify.toCSVFormat();
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
            System.out.println("Ship not found.");
        }
    }

    @Override
    public String toCSVFormat() {
        StringBuilder sb = new StringBuilder(super.toCSVFormat());

        String containersCSV = super.getContainersCSV();

        if (containersCSV.isEmpty()) {
            sb.append(",");
        } else {
            sb.append(",").append(containersCSV);
        }

        return sb.toString();
    }

}
