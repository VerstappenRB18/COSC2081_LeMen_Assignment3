package vehicle;

import ports.Ports;
import container.Container;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        List<Ports> portsList = new ArrayList<>();
        List<Vehicle> vehicleList = new ArrayList<>();
        List<Container> containerList = new ArrayList<>();

        try {
            portsList = Ports.readFromFile("ports.csv");
        } catch (IOException e) {
            System.err.println("Error loading ports from file: " + e.getMessage());
        }
        try {
            containerList = Container.readFromFile("containers.txt");
        } catch (IOException e) {
            System.err.println("Error loading containers from file: " + e.getMessage());
        }
        try {
            vehicleList = loadVehiclesFromFile("vehicles.csv", portsList);
        } catch (IOException e) {
            System.err.println("Error loading vehicle from file: " + e.getMessage());
        }

        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Create a new Truck");
            System.out.println("2. Create a new Ship");
            System.out.println("3. Add a Container to a Vehicle");
            System.out.println("4. Unload a Container from a Vehicle");
            System.out.println("5. Move a Vehicle to a different Port");
            System.out.println("6. Display all Vehicles");
            System.out.println("7. Display all Containers");
            System.out.println("8. Calculate daily fuel consumption for a vehicle");
            System.out.println("9. Refuel vehicle");
            System.out.println("10. Exit");
            System.out.print("Choose an option (1-9): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Available ports:");
                    for (int i = 0; i < portsList.size(); i++) {
                        System.out.println((i + 1) + ". " + portsList.get(i).getId());
                    }
                    System.out.print("Select a port by entering its number: ");
                    int portIndexForTruck = scanner.nextInt() - 1;
                    if (portIndexForTruck < 0 || portIndexForTruck >= portsList.size()) {
                        System.out.println("Invalid choice. Please try again.");
                        break;
                    }
                    Ports currentPortForTruck = portsList.get(portIndexForTruck);
                    Vehicle newTruck = Truck.createVehicle(scanner, currentPortForTruck, portsList);
                    vehicleList.add(newTruck);
                    break;
                case 2:
                    System.out.println("Available ports:");
                    for (int i = 0; i < portsList.size(); i++) {
                        System.out.println((i + 1) + ". " + portsList.get(i).getId());
                    }
                    System.out.print("Select a port by entering its number: ");
                    int portIndexForShip = scanner.nextInt() - 1;
                    if (portIndexForShip < 0 || portIndexForShip >= portsList.size()) {
                        System.out.println("Invalid choice. Please try again.");
                        break;
                    }
                    Ports currentPortForShip = portsList.get(portIndexForShip);
                    Vehicle newShip = Ship.createVehicle(scanner, currentPortForShip, portsList);
                    vehicleList.add(newShip);
                    break;
                case 3:
                    System.out.print("Enter Vehicle ID to add container to: ");
                    String vehicleId = scanner.next();
                    Vehicle vehicle = findVehicleById(vehicleList, vehicleId);
                    if (vehicle == null) {
                        System.out.println("Vehicle not found.");
                        break;
                    }
                    System.out.println("Available Containers:");
                    for (int i = 0; i < containerList.size(); i++) {
                        System.out.println((i + 1) + ". " + containerList.get(i).toString());
                    }
                    System.out.print("Enter the ID of the Container to add: ");
                    scanner.nextLine();
                    String containerId = scanner.nextLine();
                    Container container = findContainerById(containerList, containerId);
                    if (container != null) {
                        vehicle.addContainer(container);
                        System.out.println("Container added to vehicle.");
                    } else {
                        System.out.println("Invalid container ID. Please try again.");
                    }
                    break;
                case 4:
                    System.out.print("Enter Vehicle ID to unload a container from: ");
                    String unloadVehicleId = scanner.next();
                    Vehicle unloadVehicle = findVehicleById(vehicleList, unloadVehicleId);
                    if (unloadVehicle == null) {
                        System.out.println("Vehicle not found.");
                        break;
                    }

                    if (unloadVehicle.getContainers().isEmpty()) {
                        System.out.println("No container to unload.");
                        break;
                    }

                    Container unloadContainer = unloadVehicle.getContainers().get(0);
                    if (unloadVehicle.unloadContainer(unloadContainer, unloadVehicle.getCurrentPort())) {
                        System.out.println("Container unloaded successfully.");
                    } else {
                        System.out.println("Failed to unload container.");
                    }
                    break;
                case 5:
                    break;
                case 6:
                    for (Vehicle v : vehicleList) {
                        System.out.println(v);
                    }
                    break;
                case 7:
                    for (Container c : containerList) {
                        System.out.println(c);
                    }
                    break;
                case 8:
                    System.out.print("Enter Vehicle ID to calculate daily fuel consumption for: ");
                    String vehicleIdToCalculateFuel = scanner.next();
                    Vehicle vehicleToCalculateFuel = findVehicleById(vehicleList, vehicleIdToCalculateFuel);
                    if (vehicleToCalculateFuel == null) {
                        System.out.println("Vehicle not found.");
                        break;
                    }
                    System.out.print("Enter the daily distance traveled by the vehicle (in km): ");
                    double dailyDistance = scanner.nextDouble();
                    double dailyFuelConsumption = vehicleToCalculateFuel.calculateDailyFuelConsumption(dailyDistance);
                    System.out.println("The daily fuel consumption for the vehicle is: " + dailyFuelConsumption + " liters");
                    break;
                case 9:
                    Vehicle.refuel(scanner, vehicleList);
                    break;
                case 10:
                    System.out.println("Saving data...");
                    saveAllData(vehicleList, "vehicles.csv");
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void saveAllData(List<Vehicle> vehicleList, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Vehicle vehicle : vehicleList) {
                if (vehicle != null) {
                    writer.write(vehicle.toCSVFormat());
                    writer.newLine();
                }
            }
        }
    }

    public static Vehicle findVehicleById(List<Vehicle> vehicleList, String id) {
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getId().equals(id)) {
                return vehicle;
            }
        }
        return null;
    }

    public static Container findContainerById(List<Container> containerList, String id) {
        for (Container container : containerList) {
            if (container.getId().equals(id)) {
                return container;
            }
        }
        return null;
    }

    public static List<Vehicle> loadVehiclesFromFile(String filePath, List<Ports> portsList) throws IOException {
        List<Vehicle> vehicleList = new ArrayList<>();
        int maxVehicleId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Vehicle vehicle;
                Ports currentPort = findPortById(portsList, data[5]);
                if ("Truck".equals(data[6])) {
                    vehicle = new Truck(data, currentPort);
                } else {
                    vehicle = new Ship(data, currentPort);
                }
                vehicleList.add(vehicle);
                int vehicleIdNumber = Integer.parseInt(data[0].substring(2));
                maxVehicleId = Math.max(maxVehicleId, vehicleIdNumber);
            }
        }
        Vehicle.setVehicleCounter(maxVehicleId);
        return vehicleList;
    }

    public static Ports findPortById(List<Ports> portsList, String id) {
        for (Ports port : portsList) {
            if (port.getId().equals(id)) {
                return port;
            }
        }
        return null;
    }
}
