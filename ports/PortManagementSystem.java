package ports;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class PortManagementSystem {
    public static void main(String[] args) {
        List<Ports> portsList;
        Scanner scanner = new Scanner(System.in);

        try {
            portsList = Ports.readFromFile("ports.txt");
        } catch (IOException e) {
            System.err.println("Could not read from the file: " + e.getMessage());
            return;
        }

        while (true) {
            try {
                System.out.println("Ports Management Menu");
                System.out.println("1. Create a new port");
                System.out.println("2. View all ports");
                System.out.println("3. Update a port");
                System.out.println("4. Delete a port");
                System.out.println("5. Exit");
                System.out.print("Choose an option (1-5): ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();

                        System.out.print("Enter latitude: ");
                        double latitude = scanner.nextDouble();

                        System.out.print("Enter longitude: ");
                        double longitude = scanner.nextDouble();

                        System.out.print("Enter storing capacity: ");
                        double storingCapacity = scanner.nextDouble();

                        System.out.print("Enter landing ability (true/false): ");
                        boolean landingAbility = scanner.nextBoolean();

                        scanner.nextLine();

                        portsList.add(new Ports(name, latitude, longitude, storingCapacity, landingAbility, null, null, null));

                        try {
                            Ports.saveAllToFile("ports.txt", portsList);
                        } catch (IOException e) {
                            System.err.println("Could not save to the file: " + e.getMessage());
                        }
                    }
                    case 2 -> {
                        for (Ports port : portsList) {
                            System.out.println(port.toString());
                        }
                    }
                    case 3 -> {
                        System.out.println("--- Update a Port ---");
                        System.out.print("Enter port ID to update: ");
                        String updateId = scanner.nextLine();

                        int indexToUpdate = -1;
                        for (int i = 0; i < portsList.size(); i++) {
                            if (portsList.get(i).getId().equals(updateId)) {
                                indexToUpdate = i;
                                break;
                            }
                        }

                        if (indexToUpdate != -1) {
                            Ports portToUpdate = portsList.get(indexToUpdate);

                            System.out.print("Enter new name: ");
                            String name = scanner.nextLine();
                            portToUpdate.setName(name);

                            System.out.print("Enter new latitude: ");
                            double latitude = scanner.nextDouble();

                            System.out.print("Enter new longitude: ");
                            double longitude = scanner.nextDouble();

                            System.out.print("Enter new storing capacity: ");
                            double storingCapacity = scanner.nextDouble();

                            System.out.print("Enter new landing ability (true/false): ");
                            boolean landingAbility = scanner.nextBoolean();

                            scanner.nextLine();

                            try {
                                Ports.saveAllToFile("ports.txt", portsList);
                            } catch (IOException e) {
                                System.err.println("Could not save to the file: " + e.getMessage());
                            }

                            System.out.println("Port updated successfully.");
                        } else {
                            System.err.println("No port found with ID: " + updateId);
                        }
                    }
                    case 4 -> {
                        System.out.println("--- Delete a Port ---");
                        System.out.print("Enter port ID to delete: ");
                        String deleteId = scanner.nextLine();

                        int indexToDelete = -1;
                        for (int i = 0; i < portsList.size(); i++) {
                            if (portsList.get(i).getId().equals(deleteId)) {
                                indexToDelete = i;
                                break;
                            }
                        }

                        if (indexToDelete != -1) {
                            portsList.remove(indexToDelete);

                            try {
                                Ports.saveAllToFile("ports.txt", portsList);
                            } catch (IOException e) {
                                System.err.println("Could not save to the file: " + e.getMessage());
                            }

                            System.out.println("Port deleted successfully.");
                        } else {
                            System.err.println("No port found with ID: " + deleteId);
                        }
                    }
                    case 5 -> {
                        System.out.println("Goodbye! Have a nice day.");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }
}
