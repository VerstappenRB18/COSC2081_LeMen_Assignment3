package vehicle;

import java.io.*;
import java.util.*;
import ports.Ports;


public class Main {
    public static void main(String[] args) {
        List<Vehicle> vehicleList = new ArrayList<>();
        String filename = "vehicles.csv";  // Adjust the file name/path as necessary

        try {
            initializeVehicleCounter(filename, vehicleList);
        } catch (IOException e) {
            System.out.println("An error occurred while initializing the vehicle counter.");
        }
    }
    public static void initializeVehicleCounter(String filename, List<Vehicle> vehicleList) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                String id = parts[0];
                String name = parts[1];
                double carryingCapacity = Double.parseDouble(parts[3]);
                double currentFuel = Double.parseDouble(parts[4]);
                double fuelCapacity = Double.parseDouble(parts[5]);
                String vehicleType = id.split("-")[0];  // Determining the vehicle type based on the ID prefix

                Vehicle vehicle = null;

                if (vehicleType.equals("tr")) {
                    Truck.TruckType truckType = Truck.TruckType.valueOf(parts[2].toUpperCase());
                    vehicle = new Truck(name, truckType, carryingCapacity, fuelCapacity, currentFuel);
                }
                else if(vehicleType.equals("sh")) {
                    // For now, setting currentPort to null. Replace it with actual logic later
                    Ports currentPort = null;
                    vehicle = new Ship(name, carryingCapacity, fuelCapacity, currentFuel, currentPort);
                }
                else {
                    System.out.println("Unsupported vehicle type in data file: " + vehicleType);
                    continue;
                }

                vehicleList.add(vehicle);

                // Update vehicle counter
                String[] idParts = id.split("-");
                if (idParts.length < 2) {
                    System.err.println("Invalid ID format in file: " + id);
                    continue;
                }
                int idNumber = Integer.parseInt(idParts[1]);
                Vehicle.setVehicleCounter(Math.max(Vehicle.getVehicleCounter(), idNumber + 1));
            }
        } catch (IOException e) {
            // File might not exist yet, which is fine
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Create a new vehicle");
            System.out.println("2. Update an existing vehicle");
            System.out.println("3. Delete an existing vehicle");
            System.out.println("4. Display all vehicle data");
            System.out.println("5. Exit");

            String choice = reader.readLine().trim();

            switch (choice) {
                case "1":
                    createVehicle(vehicleList, filename);
                    break;
                case "2":
                    updateVehicle(vehicleList);
                    // Save the updated list to the file
                    saveVehicleToFile(vehicleList, filename);
                    break;
                case "3":
                    deleteVehicle(vehicleList, filename);
                    break;
                case "4":
                    displayAllVehicleData(vehicleList);
                    break;
                case "5":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }
    public static void createVehicle(List<Vehicle> vehicleList, String filename) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Enter vehicle name: ");
            String name = reader.readLine().trim();

            System.out.print("Enter carrying capacity: ");
            double carryingCapacity = Double.parseDouble(reader.readLine());

            System.out.print("Enter fuel capacity: ");
            double fuelCapacity = Double.parseDouble(reader.readLine());

            System.out.print("Enter current fuel: ");
            double currentFuel = Double.parseDouble(reader.readLine());

            System.out.print("Enter vehicle type (truck/ship): ");
            String vehicleType = reader.readLine().trim().toLowerCase();

            Vehicle vehicle = null;

            if (vehicleType.equals("truck")) {
                System.out.print("Enter truck type (basic/reefer/tanker): ");
                String truckTypeStr = reader.readLine().trim().toLowerCase();
                Truck.TruckType truckType = Truck.TruckType.valueOf(truckTypeStr.toUpperCase());

                vehicle = new Truck(name, truckType, carryingCapacity, fuelCapacity, currentFuel);
            } else if (vehicleType.equals("ship")) {
                // You will need to add proper handling for creating a Ship object here
                System.out.println("Ship creation not supported at the moment.");
                return;
            } else {
                System.out.println("Invalid vehicle type.");
                return;
            }

            vehicleList.add(vehicle);
            saveVehicleToFile(vehicleList, filename);

            System.out.println("Vehicle created successfully.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("An error occurred while creating the vehicle. Please check your input and try again.");
        }
    }

    public static void updateVehicle(List<Vehicle> vehicleList) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("Enter vehicle ID to update: ");
            String id = reader.readLine();

            Vehicle existingVehicle = null;

            for (Vehicle vehicle : vehicleList) {
                if (vehicle.getId().equals(id)) {
                    existingVehicle = vehicle;
                    break;
                }
            }

            if (existingVehicle == null) {
                System.err.println("No vehicle found with ID: " + id);
                continue;
            }

            System.out.print("Enter new name for the vehicle: ");
            String name = reader.readLine();
            existingVehicle.name = name;

            while (true) {
                System.out.print("Enter new carrying capacity for the vehicle (a positive number): ");
                try {
                    double carryingCapacity = Double.parseDouble(reader.readLine());
                    if (carryingCapacity > 0) {
                        existingVehicle.carryingCapacity = carryingCapacity;
                        break;
                    } else {
                        System.err.println("Carrying capacity must be a positive number.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid input. Please enter a valid number.");
                }
            }

            while (true) {
                System.out.print("Enter new fuel capacity for the vehicle (a positive number): ");
                try {
                    double fuelCapacity = Double.parseDouble(reader.readLine());
                    if (fuelCapacity > 0) {
                        existingVehicle.fuelCapacity = fuelCapacity;
                        break;
                    } else {
                        System.err.println("Fuel capacity must be a positive number.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid input. Please enter a valid number.");
                }
            }

            System.out.println("Vehicle updated successfully.");
            return;
        }
    }
    public static void saveVehicleToFile(List<Vehicle> vehicleList, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            for (Vehicle vehicle : vehicleList) {
                writer.write(vehicle.toCSVFormat());
                writer.newLine();
            }
        }
    }

    public static void deleteVehicle(List<Vehicle> vehicleList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter vehicle ID to delete: ");
        String id = reader.readLine();

        Iterator<Vehicle> iterator = vehicleList.iterator();
        while (iterator.hasNext()) {
            Vehicle vehicle = iterator.next();
            if (vehicle.getId().equals(id)) {
                iterator.remove();
                saveVehicleToFile(vehicleList, filename);
                System.out.println("Vehicle deleted successfully.");
                return;
            }
        }
        System.err.println("No vehicle found with ID: " + id);
    }

        public static void displayAllVehicleData(List<Vehicle> vehicleList) {
            System.out.println("Displaying all vehicle data:");
            for (Vehicle vehicle : vehicleList) {
                System.out.println(vehicle.toString());
            }
        }
}
