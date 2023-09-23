package vehicle;

import ports.Ports;
import container.Container;
import User.*;

import java.io.*;
import java.util.*;

public class Menu {
    public static void main(String[] args, User loggedInUser) throws IOException {
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
            vehicleList = loadVehiclesFromFile("vehicles.csv", portsList, containerList);
            Vehicle.updateVehicleCounters(vehicleList);
        } catch (IOException e) {
            System.err.println("Error loading vehicle from file: " + e.getMessage());
        }

        while (true) {
            try {
                System.out.println("Menu:");
                System.out.println("1. Display all Vehicles");
                System.out.println("2. Display all Containers");
                System.out.println("3. Add a Container to a Vehicle");
                System.out.println("4. Unload a Container from a Vehicle");
                System.out.println("5. Calculate daily fuel consumption for a vehicle");
                System.out.println("6. Refuel vehicle");
                System.out.println("7. Exit");

                if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                    System.out.println("-------- Admin Options ----------");
                    System.out.println("8. Create a new Truck");
                    System.out.println("9. Create a new Ship");
                    System.out.println("10. Delete a vehicle");
                    System.out.println("11. Modify Truck");
                    System.out.println("12. Modify Ship");
                    System.out.println("---------------------------------");
                }

                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();


                switch (choice) {
                    case 1:
                        for (Vehicle v : vehicleList) {
                            System.out.println(loggedInUser.getPortId());
                            System.out.println(v);
                        }
                        break;
                    case 2:
                        for (Container c : containerList) {
                            Vehicle associatedVehicle = Vehicle.findVehicleByContainerId(vehicleList, c.getId());
                            if (associatedVehicle == null) {
                                continue;
                            }
                            if (loggedInUser.getUserRole() != User.UserRole.MANAGER ||
                                    (loggedInUser.getPortId().equals(associatedVehicle.getCurrentPort().getId()))) {
                                System.out.println(c);
                            }
                        }
                        break;
                    case 3:
                        Vehicle.addContainer(scanner, vehicleList, containerList);
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
                    case 6:
                        Vehicle.refuel(scanner, vehicleList);
                        break;
                    case 7:
                        System.out.println("Saving data...");
                        saveAllData(vehicleList, "vehicles.csv");
                        System.out.println("Exiting...");
                        return;
                    case 8:
                        if (loggedInUser.getUserRole() == User.UserRole.MANAGER) {
                            System.out.println("You are not authorized to create a new Truck.");
                        } else {
                            vehicleList.add(Truck.addVehicle(scanner, portsList));
                        }
                        break;
                    case 9:
                        if (loggedInUser.getUserRole() == User.UserRole.MANAGER) {
                            System.out.println("You are not authorized to create a new Ship.");
                        } else {
                            vehicleList.add(Ship.addVehicle(scanner, portsList));
                        }
                        break;
                    case 10:
                        if (loggedInUser.getUserRole() == User.UserRole.MANAGER) {
                            System.out.println("You are not authorized to delete the vehicle.");
                        } else {
                            Vehicle.deleteVehicle(vehicleList, scanner, "vehicles.csv");
                        }
                        break;
                    case 11:
                        if (loggedInUser.getUserRole() == User.UserRole.MANAGER) {
                            System.out.println("You are not authorized to modify vehicles.");
                        } else {
                            Truck.modifyTruckAttributes(vehicleList, scanner);
                        }
                        break;
                    case 12:
                        if (loggedInUser.getUserRole() == User.UserRole.MANAGER) {
                            System.out.println("You are not authorized to modify vehicles.");
                        } else {
                            Ship.modifyShipAttributes(vehicleList, scanner);
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 10.");
                scanner.next(); // Clear the invalid input
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
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

    public static List<Vehicle> loadVehiclesFromFile(String filePath, List<Ports> portsList, List<Container> containerList) throws IOException {
        List<Vehicle> vehicleList = new ArrayList<>();
        int maxVehicleId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) {
                    System.err.println("Insufficient data in line: " + line);
                    continue;
                }
                Ports currentPort = findPortById(portsList, data[5]);
                if (currentPort == null) {
                    System.err.println("Invalid port ID in line: " + line);
                    continue;
                }
                Vehicle vehicle = null;
                if ("Truck".equals(data[6])) {
                    vehicle = new Truck(data, currentPort);
                } else if ("Ship".equals(data[6])) {
                    vehicle = new Ship(data, currentPort);
                } else {
                    System.err.println("Invalid vehicle type in line: " + line);
                    continue;
                }

                // Load containers
                String[] containerIds;
                int containerIndex = "Truck".equals(data[6]) ? 8 : 7;
                if (data.length > containerIndex) {
                    containerIds = data[containerIndex].split(";");
                    for (String id : containerIds) {
                        Container container = Vehicle.findContainerById(containerList, id);
                        if (container != null) {
                            vehicle.getContainers().add(container);
                        }
                    }
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
