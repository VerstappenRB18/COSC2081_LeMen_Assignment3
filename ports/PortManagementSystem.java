package ports;

import trip.Trip;
import vehicle.Ship;
import vehicle.Truck;
import vehicle.Vehicle;
import container.Container;

import javax.sound.sampled.Port;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static vehicle.Main.findPortById;

public class PortManagementSystem {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        List<Ports> portsList = new ArrayList<>();
        List<Vehicle> vehicleList = new ArrayList<>();
        List<Container> containerList = new ArrayList<>();
        List<Trip> tripList = new ArrayList<>();

        // Load ports, vehicles, containers, and trips from files
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
            System.err.println("Error loading vehicles from file: " + e.getMessage());
        }

        try {
            tripList = Trip.readFromFile("trips.csv", vehicleList, portsList);
        } catch (IOException e) {
            System.err.println("Error loading trips from file: " + e.getMessage());
        }

        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Create a new port");
            System.out.println("2. View all ports");
            System.out.println("3. Update existing port");
            System.out.println("4. Delete existing port");
            System.out.println("5. Create a new trip");
            System.out.println("6. View all trips");
            System.out.println("7. Update existing trip");
            System.out.println("8. Delete existing trip");
            System.out.println("9. List all ship from port");
            System.out.println("10. List all trip in a day");
            System.out.println("11. List all trip in a span of time");
            System.out.println("12. Exit");
            System.out.print("Choose an option (1-12): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> Ports.createPort(portsList, Ports.filename);
                case 2 -> {
                    for (Ports port : portsList) {
                        System.out.println(port);
                    }
                }
                case 3 -> Ports.updatePort(portsList, Ports.filename);
                case 4 -> Ports.deletePort(portsList, Ports.filename);
                case 5 -> Trip.createTrip(tripList, vehicleList, portsList, Trip.filename);
                case 6 -> Trip.viewAllTrips(tripList);
                case 7 -> Trip.updateTrip(tripList, vehicleList, portsList, Trip.filename);
                case 8 -> Trip.deleteTrip(tripList, Trip.filename);
                case 9 -> Ports.listAllShips(portsList, vehicleList);
                case 10 -> Trip.listTripsOnGivenDay(tripList);
                case 11 -> Trip.listTripsFromDayAToDayB(tripList);
                case 12 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
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

                // Extract the vehicle ID number and find the maximum ID number
                int vehicleIdNumber = Integer.parseInt(data[0].substring(2));
                maxVehicleId = Math.max(maxVehicleId, vehicleIdNumber);
            }
        }

        // Set the vehicleCounter in the Vehicle class to the highest ID number found
        Vehicle.setVehicleCounter(maxVehicleId);

        return vehicleList;
    }
}