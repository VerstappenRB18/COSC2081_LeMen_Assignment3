import ports.PortManagementSystem;
import container.*;
import User.User;
import ports.Ports;
import vehicle.*;
import User.AdminActions;

import java.io.*;
import java.util.*;

import static User.User.userList;
import static vehicle.Menu.loadVehiclesFromFile;

public class Main {

    public static void main(String[] args) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
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

        System.out.println("COSC2081 GROUP ASSIGNMENT");
        System.out.println("CONTAINER PORT MANAGEMENT SYSTEM");
        System.out.println("Instructor: Mr. Minh Vu & Dr. Phong Ngo");
        System.out.println("Group: LeMen");
        System.out.println("s3986287, Nguyen Vinh Gia Bao");
        System.out.println("s3979654, Le Viet Bao");
        System.out.println("s3978554, To Bao Minh Hoang");
        System.out.println("s3979512, Luu Van Thien Toan");
        // Attempt login
        User loggedInUser = null;
        while (loggedInUser == null) {
            System.out.println("\nPlease log in to continue.");
            loggedInUser = User.attemptLogin(scanner, userList);
        }

        // Display the main menu
        displayMainMenu(scanner, loggedInUser);
    }

    public static void displayMainMenu(Scanner scanner, User loggedInUser) throws IOException {
        List<Ports> portsList;
        try {
            portsList = Ports.readFromFile("ports.csv");
        while (true) {
            try {
                System.out.println("Please choose your desired option");
                System.out.println("=================================");
                System.out.println("1. Ports and Trips");
                System.out.println("2. Container");
                System.out.println("3. Vehicle");
                System.out.println("4. Exit");

                // Check if the user is an admin to display additional options
                if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                    System.out.println("-------- Admin Options ----------");
                    System.out.println("5. Create Manager");
                    System.out.println("6. Read Managers");
                    System.out.println("7. Update Manager");
                    System.out.println("8. Delete Manager");
                    System.out.println("---------------------------------");
                }

                System.out.println("=================================");

                int option = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                if (loggedInUser.getUserRole() == User.UserRole.MANAGER && option > 4) {
                    throw new InputMismatchException("Invalid option for manager role.");
                }

                switch (option) {
                    case 1:
                        String[] portManagementArgs = {};
                        PortManagementSystem.main(portManagementArgs, loggedInUser);
                        break;
                    case 2:
                        container.Menu.displayMenu();
                        break;
                    case 3:
                        String[] vehicleMenuArgs = {};
                        vehicle.Menu.main(vehicleMenuArgs, loggedInUser);
                        break;
                    case 4:
                        System.out.println("Goodbye!");
                        return;
                    case 5:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            AdminActions.createManager(scanner, userList, portsList);
                        }
                        break;
                    case 6:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            AdminActions.readManagers(userList);
                        }
                        break;
                    case 7:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            AdminActions.updateManager(scanner, userList, portsList);
                        }
                        break;
                    case 8:
                        if (loggedInUser.getUserRole() == User.UserRole.ADMIN) {
                            AdminActions.deleteManager(scanner, userList);
                        }
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (InputMismatchException e) {
                String validRange = (loggedInUser.getUserRole() == User.UserRole.ADMIN) ? "1 and 8" : "1 and 4";
                System.out.println("Invalid input. Please enter a number between " + validRange + ".");
                scanner.next(); // Clear the invalid input
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

