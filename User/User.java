package User;
import ports.Ports;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class User {
        private String username;
        private String password;
        private UserRole userRole;

        private static List<User> userList;
    enum UserRole {
        ADMIN,
        MANAGER,
    }
    public User(String username, String password, UserRole userRole) {
        this.username = username;
        this.password = password;
        this.userRole = userRole;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Enum getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }


    public static boolean findUserByUsername(String username, List<User> userList) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }


    public static boolean findUserByPassword(String password, List<User> userList) {
        for (User user : userList) {
            if (user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }


    private static void login(Scanner scanner, List<User> userList) {
        System.out.println("\nPlease enter your username:");
        String username = scanner.next();

        System.out.println("Please enter your password:");
        String password = scanner.next();

        // Find user by username and password
        User user = null;
        for (User u : userList) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                user = u;
                System.out.println(user.getUserRole());
                break;
            }
        }
//hello
        if (user != null) {
            // Successful login
            System.out.println("Login successful! Welcome, " + user.getUsername() + " (" + user.getUserRole() + ")\n");
//        menu(scanner);
        } else {
            // Invalid credentials
            System.out.println("Incorrect username or password. Please try again.");
        }
    }


    public static List<User> readFromFile(String filename) throws IOException {
        List<User> userList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                String username = details[0];
                String password = details[1];
                UserRole userRole = UserRole.valueOf(details[2]);
                User user = new User(username,password,userRole);
                userList.add(user);
            }
        }
        return userList;
    }




    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private static final String PORT_MANAGER_USERNAME = "manager";
    private static final String PORT_MANAGER_PASSWORD = "manager123";

    private static final String[] PORTS = {"Port A", "Port B", "Port C", "Port D"};
    private static final String[] VEHICLES = {"Vehicle 1", "Vehicle 2", "Vehicle 3", "Vehicle 4"};
    private static final String[] CONTAINERS = {"Container 1", "Container 2", "Container 3", "Container 4"};

    public static void main(String[] args) {
        try {
            userList = User.readFromFile("user.txt");
        } catch (IOException e) {
            System.err.println("Error loading user from file: " + e.getMessage());
        }
        System.out.println(userList);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the user system!");

        int choice;
        do {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            choice = scanner.nextInt();

            switch(choice) {
                case 1:
                    login(scanner, userList);
                    break;
                case 2:
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while(choice != 2);

        scanner.close();
    }


//    private static void menu(Scanner scanner) {
//        int choice;
//        while (true){
//            System.out.println("Please choose an option:");
//            System.out.println("1. Add entity");
//            System.out.println("2. Remove entity");
//            System.out.println("3. View information");
//            System.out.println("4. Modify information");
//            System.out.println("5. Logout");
//}
}

