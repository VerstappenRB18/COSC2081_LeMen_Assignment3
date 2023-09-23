package User;

import ports.Ports;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class AdminActions {

    public static void createManager(Scanner scanner, List<User> userList, List<Ports> portsList) throws IOException {
        try {
            portsList = Ports.readFromFile("ports.csv");
        } catch (IOException e) {
            System.err.println("Error loading ports from file: " + e.getMessage());
        }

        try {
            userList = User.readFromFile("user.txt", portsList);
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
        }

        System.out.print("Enter new manager's username: ");
        String username = scanner.next();

        // Check if username already exists
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                System.out.println("Username already exists. Choose another username.");
                return;
            }
        }

        System.out.print("Enter new manager's password: ");
        String password = scanner.next();

        System.out.println("Available ports:");
        for (Ports port : portsList) {
            System.out.println(port.getId());
        }
        System.out.print("Select a port by entering its ID: ");
        String portId = scanner.next();

        if (isPortManaged(userList, portId)) {
            System.out.println("This port already has a manager. Choose another port.");
            return;
        }

        User newManager = new User(username, password, User.UserRole.MANAGER, portId);
        userList.add(newManager);
        System.out.println("New manager created.");
        saveUsersToFile(userList, "user.txt");
    }

    public static void readManagers(List<User> userList) {
        List<Ports> portsList =new ArrayList<>();
        try {
            portsList = Ports.readFromFile("ports.csv");
        } catch (IOException e) {
            System.err.println("Error loading ports from file: " + e.getMessage());
        }

        try {
            userList = User.readFromFile("user.txt", portsList);
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
        }

        System.out.println("List of Managers:");
        for (User user : userList) {
            if (user.getUserRole() == User.UserRole.MANAGER) {
                System.out.println("Username: " + user.getUsername() + ", Port ID: " + user.getPortId());
            }
        }
    }

    public static void updateManager(Scanner scanner, List<User> userList, List<Ports> portsList) throws IOException {
        try {
            portsList = Ports.readFromFile("ports.csv");
        } catch (IOException e) {
            System.err.println("Error loading ports from file: " + e.getMessage());
        }

        try {
            userList = User.readFromFile("user.txt", portsList);
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
        }

        System.out.print("Enter the username of the manager you want to update: ");
        String username = scanner.next();

        for (User user : userList) {
            if (user.getUsername().equals(username) && user.getUserRole() == User.UserRole.MANAGER) {
                System.out.print("Enter new password: ");
                String newPassword = scanner.next();
                user.setPassword(newPassword);

                System.out.println("Available ports:");
                for (Ports port : portsList) {
                    System.out.println(port.getId());
                }
                System.out.print("Select a new port by entering its ID: ");
                String newPortId = scanner.next();

                if (isPortManaged(userList, newPortId)) {
                    System.out.println("This port already has a manager. Choose another port.");
                    return;
                }

                user.setPortId(newPortId);
                System.out.println("Port ID updated.");
                saveUsersToFile(userList, "user.txt");
                return;
            }
        }
        System.out.println("Manager not found.");
    }

    public static void deleteManager(Scanner scanner, List<User> userList) throws IOException {
        List<Ports> portsList =new ArrayList<>();
        try {
            portsList = Ports.readFromFile("ports.csv");
        } catch (IOException e) {
            System.err.println("Error loading ports from file: " + e.getMessage());
        }

        try {
            userList = User.readFromFile("user.txt", portsList);
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
        }

        System.out.print("Enter the username of the manager you want to delete: ");
        String username = scanner.next();

        Iterator<User> iterator = userList.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUsername().equals(username) && user.getUserRole() == User.UserRole.MANAGER) {
                iterator.remove();
                System.out.println("Manager deleted.");
                saveUsersToFile(userList, "user.txt");
                return;
            }
        }
        System.out.println("Manager not found.");
    }

    public static boolean isPortManaged(List<User> userList, String portId) {
        for (User user : userList) {
            if (user.getUserRole() == User.UserRole.MANAGER && user.getPortId().equals(portId)) {
                return true;
            }
        }
        return false;
    }

    public static void saveUsersToFile(List<User> userList, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (User user : userList) {
                StringBuilder sb = new StringBuilder();
                sb.append(user.getUsername()).append(",");
                sb.append(user.getPassword()).append(",");
                sb.append(user.getUserRole());
                if (user.getUserRole() == User.UserRole.MANAGER) {
                    sb.append(",").append(user.getPortId());
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }
}
