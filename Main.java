import ports.PortManagementSystem;
import container.Container;
import container.Menu;
import User.User;
import vehicle.*;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalExceptionHandler());
        Scanner scanner = new Scanner(System.in);

        // Load user data
        List<User> userList = User.readFromFile("user.txt");

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
            System.out.println("Please log in to continue.");
            loggedInUser = User.attemptLogin(scanner, userList);
        }

        // Display the main menu
        displayMainMenu(scanner);
    }

    public static void displayMainMenu(Scanner scanner) throws IOException {
        while (true) {
            try {
                System.out.println("Please choose your desired option");
                System.out.println("=================================");
                System.out.println("1. Ports and Trips");
                System.out.println("2. Container");
                System.out.println("3. Vehicle");
                System.out.println("4. Exit");
                System.out.println("=================================");

                int option = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (option) {
                    case 1:
                        String[] portManagementArgs = {};
                        PortManagementSystem.main(portManagementArgs);
                        break;
                    case 2:
                        container.Menu.displayMenu();
                        break;
                    case 3:
                        String[] vehicleMenuArgs = {};
                        vehicle.Menu.main(vehicleMenuArgs);
                        break;
                    case 4:
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                scanner.next(); // Clear the invalid input
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }
}

