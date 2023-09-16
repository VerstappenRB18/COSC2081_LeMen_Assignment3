package ports;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class PortManagementSystem {
    public static void main(String[] args) throws IOException {
        List<Ports> portsList;
        Scanner scanner = new Scanner(System.in);

        // Load existing data at the start of the program
        portsList = Ports.readFromFile("ports.txt");

        while (true) {
            System.out.println("Ports Management Menu");
            System.out.println("1. Create a new port");
            System.out.println("2. View all ports");
            System.out.println("3. Update a port");
            System.out.println("4. Delete a port");
            System.out.println("5. Exit");
            System.out.print("Choose an option (1-5): ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1 -> {
                    // Prompt the user for details and create a new Port
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
                    scanner.nextLine();  // Consume newline
                    portsList.add(new Ports(name, latitude, longitude, storingCapacity, landingAbility, null, null, null));
                    Ports.saveAllToFile("ports.txt", portsList);  // Automatically save after creating a port
                }
                case 2 -> {
                    // View all ports
                    for (Ports port : portsList) {
                        System.out.println(port.toString());
                    }
                }
                case 3 -> {
                    System.out.println("--- Update a Port ---");
                    System.out.print("Enter port ID to update: ");
                    String updateId = scanner.nextLine();

                    // Find the index of the port with the specified ID
                    int indexToUpdate = -1;
                    for (int i = 0; i < portsList.size(); i++) {
                        if (portsList.get(i).getId().equals(updateId)) {
                            indexToUpdate = i;
                            break;
                        }
                    }

                    // If a port with the specified ID is found, prompt for new details
                    if (indexToUpdate != -1) {
                        Ports portToUpdate = portsList.get(indexToUpdate);

                        System.out.print("Enter new name: ");
                        String name = scanner.nextLine();
                        portToUpdate.setName(name);

                        System.out.print("Enter new latitude (numeric value): ");
                        double latitude = scanner.nextDouble();
                        portToUpdate.setLatitude(latitude);

                        System.out.print("Enter new longitude (numeric value): ");
                        double longitude = scanner.nextDouble();
                        portToUpdate.setLongitude(longitude);

                        System.out.print("Enter new storing capacity (numeric value): ");
                        double storingCapacity = scanner.nextDouble();
                        portToUpdate.setStoringCapacity(storingCapacity);

                        System.out.print("Enter new landing ability (true/false): ");
                        boolean landingAbility = scanner.nextBoolean();
                        portToUpdate.setLandingAbility(landingAbility);

                        scanner.nextLine();  // Consume newline

                        // Save the updated list to file
                        Ports.saveAllToFile("ports.txt", portsList);  // Automatically save after updating a port
                        System.out.println("Port updated successfully.");
                    } else {
                        System.err.println("No port found with ID: " + updateId);
                    }
                }

                case 4 -> {
                    System.out.println("--- Delete a Port ---");
                    System.out.print("Enter port ID to delete: ");
                    String deleteId = scanner.nextLine();

                    // Find and remove the port with the specified ID
                    int indexToDelete = -1;
                    for (int i = 0; i < portsList.size(); i++) {
                        if (portsList.get(i).getId().equals(deleteId)) {
                            indexToDelete = i;
                            break;
                        }
                    }

                    // If a port with the specified ID is found, delete it
                    if (indexToDelete != -1) {
                        portsList.remove(indexToDelete);
                        Ports.saveAllToFile("ports.txt", portsList);  // Automatically save after deleting a port
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
        }
    }
}
