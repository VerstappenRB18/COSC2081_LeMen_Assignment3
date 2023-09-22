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
    public enum UserRole {
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

    public UserRole getUserRole() {
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
    private static String currentUserRole;


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

    public static void debugger(String currentUserRole){
        System.out.println("cUserRole is " + currentUserRole);
    }

    public static User attemptLogin(Scanner scanner, List<User> userList) {
        System.out.println("\nPlease enter your username:");
        String username = scanner.next();

        System.out.println("Please enter your password:");
        String password = scanner.next();

        // Find user by username and password
        for (User u : userList) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                System.out.println("Login successful! Welcome, " + u.getUsername() + " (" + u.getUserRole() + ")\n");
                return u;
            }
        }
        System.out.println("Incorrect username or password. Please try again.");
        return null;
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


//    public static void main(String[] args) {
//        try {
//            userList = User.readFromFile("user.txt");
//        } catch (IOException e) {
//            System.err.println("Error loading user from file: " + e.getMessage());
//        }
//        System.out.println(userList);
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Welcome to the user system!");
//
//        int choice;
//         {
//            System.out.println("\nPlease choose an option:");
//            System.out.println("1. Login");
//            System.out.println("2. Exit");
//            choice = scanner.nextInt();
//
//            switch(choice) {
//                case 1:
//                    login(scanner, userList);
//                    break;
//                case 2:
//                    System.out.println("Goodbye!");
//                    break;
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//                    break;
//            }
//        }
//
//        scanner.close();
//        debugger(currentUserRole);
//    }
}

