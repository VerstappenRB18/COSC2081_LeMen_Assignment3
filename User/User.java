package User;
import ports.Ports;

import javax.sound.sampled.Port;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class User {
    private String username;
    private String password;
    private UserRole userRole;
    private Ports portId;

    private static List<User> userList;

    public enum UserRole {
        ADMIN,
        MANAGER,
    }

    public User(String username, String password, UserRole userRole, Ports portId) {
        this.username = username;
        this.password = password;
        this.userRole = userRole;
        this.portId = portId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    private static String currentUserRole;

    public static User attemptLogin(Scanner scanner, List<User> userList) {
        System.out.print("\nPlease enter your username: ");
        String username = scanner.next();

        System.out.print("Please enter your password: ");
        String password = scanner.next();

        // Find user by username and password
        for (User u : userList) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                System.out.println("Login successful! Welcome, " + u.getUsername() + " (" + u.getUserRole() + ")\n");
                if (u.getPortId() != null) {  // Check if the user is managing a port
                    System.out.println("Port Managing: " + u.getPortId().getId());  // Print the port ID
                }
                return u;
            }
        }
        System.out.println("Incorrect username or password. Please try again.");
        return null;
    }


    public static List<User> readFromFile(String filename, List<Ports> portsList) throws IOException {
        List<User> userList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length < 3) {
                    System.out.println("Skipping line due to insufficient data: " + line);
                    continue;
                }
                String username = details[0];
                String password = details[1];
                UserRole userRole = UserRole.valueOf(details[2]);
                Ports port = null;  // Initialize port as null

                if (userRole == UserRole.MANAGER) {
                    if (details.length < 4) {
                        System.out.println("Skipping line due to insufficient data for manager: " + line);
                        continue;
                    }
                    String portId = details[3];  // Read portId as String

                    // Find the corresponding Ports object
                    port = findPortById(portsList, portId);
                    if (port == null) {
                        System.err.println("Invalid port ID in user data: " + portId);
                        continue;
                    }
                }

                User user = new User(username, password, userRole, port);  // Updated constructor
                userList.add(user);
            }
        }
        return userList;
    }


    public static Ports findPortById(List<Ports> portsList, String portId) {
        for (Ports port : portsList) {
            if (port.getId().equals(portId)) {
                return port;
            }
        }
        return null;
    }

    public Ports getPortId() {
        return portId;
    }
}