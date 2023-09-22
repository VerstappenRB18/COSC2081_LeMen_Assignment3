import ports.PortManagementSystem;
import container.Container;
import container.Menu;
import User.User;
import vehicle.*;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Load user data
        List<User> userList = User.readFromFile("user.txt");

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
            System.out.println("Please choose your desired option");
            System.out.println("=================================");
            System.out.println("1. Ports and Trips");
            System.out.println("2. Container");
            System.out.println("3. Vehicle");
            System.out.println("4. Exit");
            System.out.println("=================================");

            int option = scanner.nextInt();
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
        }
    }
}
