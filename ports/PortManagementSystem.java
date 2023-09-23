package ports;

import trip.Trip;
import vehicle.*;
import container.Container;
import User.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PortManagementSystem {
    public static void main(String[] args, User loggedInUser) {

        Scanner scanner = new Scanner(System.in);
        List<Ports> portsList = new ArrayList<>();
        List<Vehicle> vehicleList = new ArrayList<>();
        List<Container> containerList;
        List<Trip> tripList = new ArrayList<>();

        // Load ports, vehicles, containers, and trips from files
        try {
            portsList = Ports.readFromFile("ports.csv");
            containerList = Container.readFromFile("containers.txt");
            vehicleList = Menu.loadVehiclesFromFile("vehicles.csv", portsList, containerList);
            tripList = Trip.readFromFile("trips.csv", vehicleList, portsList);
        } catch (IOException e) {
            System.err.println("Error loading data from file: " + e.getMessage());
        }

        while (true) {
            try {
                System.out.println("Menu:");
                System.out.println("1. View all ports");
                System.out.println("2. View all trips");
                System.out.println("3. List all ship from port");
                System.out.println("4. List all trip in a day");
                System.out.println("5. List all trip in a span of time");
                System.out.println("6. Exit");

                if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                    System.out.println("-------- Admin Options ----------");
                    System.out.println("7. Create a new port");
                    System.out.println("8. Update existing port");
                    System.out.println("9. Delete existing port");
                    System.out.println("10. Create a new trip");
                    System.out.println("11. Update existing trip");
                    System.out.println("12. Delete existing trip");
                    System.out.println("---------------------------------");
                }

                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        for (Ports port : portsList) {
                            System.out.println(port);
                        }
                        break;
                    case 2:
                        Trip.viewAllTrips(tripList, vehicleList);
                        break;
                    case 3:
                        Ports.listAllShips(portsList, vehicleList);
                        break;
                    case 4:
                        Trip.listTripsOnGivenDay(tripList);
                        break;
                    case 5:
                        Trip.listTripsFromDayAToDayB(tripList);
                        break;
                    case 6:
                        System.out.println("Exiting...");
                        return;
                    case 7:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            Ports.createPort(portsList, Ports.filename);
                        } else {
                            System.out.println("You are not authorized to perform this action.");
                        }
                        break;
                    case 8:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            Ports.updatePort(portsList, Ports.filename);
                        } else {
                            System.out.println("You are not authorized to perform this action.");
                        }
                        break;
                    case 9:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            Ports.deletePort(portsList, Ports.filename);
                        } else {
                            System.out.println("You are not authorized to perform this action.");
                        }
                        break;
                    case 10:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            Trip.createTrip(tripList, vehicleList, portsList, Trip.filename);
                        } else {
                            System.out.println("You are not authorized to perform this action.");
                        }
                        break;
                    case 11:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            Trip.updateTrip(tripList, vehicleList, portsList, Trip.filename);
                        } else {
                            System.out.println("You are not authorized to perform this action.");
                        }
                        break;
                    case 12:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            Trip.deleteTrip(tripList, Trip.filename);
                        } else {
                            System.out.println("You are not authorized to perform this action.");
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }
}
