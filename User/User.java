package User;
import java.util.Scanner;

public class User {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    private static final String PORT_MANAGER_USERNAME = "manager";
    private static final String PORT_MANAGER_PASSWORD = "manager123";

    private static final String[] PORTS = {"Port A", "Port B", "Port C", "Port D"};
    private static final String[] VEHICLES = {"Vehicle 1", "Vehicle 2", "Vehicle 3", "Vehicle 4"};
    private static final String[] CONTAINERS = {"Container 1", "Container 2", "Container 3", "Container 4"};

    private static boolean isAdmin = false;
    private static boolean isPortManager = false;
    private static String currentPortManager = "";

    public static void main(String[] args) {
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
                    login(scanner);
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

    private static void login(Scanner scanner) {
        System.out.println("\nPlease enter your username:");
        String username = scanner.next();

        System.out.println("Please enter your password:");
        String password = scanner.next();

        isAdmin = username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD);
        isPortManager = username.equals(PORT_MANAGER_USERNAME) && password.equals(PORT_MANAGER_PASSWORD);
        if(isPortManager) {
            currentPortManager = username;
        }

        if(isAdmin || isPortManager) {
            System.out.println("Login successful!\n");
            menu(scanner);
        }
        else {
            System.out.println("Incorrect username or password. Please try again.");
        }
    }

    private static void menu(Scanner scanner) {
        int choice;
        do {
            System.out.println("Please choose an option:");
            System.out.println("1. Add entity");
            System.out.println("2. Remove entity");
            System.out.println("3. View information");
            System.out.println("4. Modify information");
            System.out.println("5. Logout");

            if(isPortManager) {
                System.out.println("-- Port Manager Operations --");
                System.out.println("6. Process containers");
            }

            choice = scanner.nextInt();

            switch(choice) {
                case 1:
                    addEntity(scanner);
                    break;
                case 2:
                    removeEntity(scanner);
                    break;
                case 3:
                    viewInformation();
                    break;
                case 4:
                    modifyInformation(scanner);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    break;
                case 6:
                    if(isPortManager) {
                        processContainers(scanner);
                    }
                    else {
                        System.out.println("Invalid choice. Please try again.");
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while(choice != 5);

        isAdmin = false;
        isPortManager = false;
        currentPortManager = "";
    }

    private static void addEntity(Scanner scanner) {
        System.out.println("Please choose the entity type to add:");
        System.out.println("1. Port");
        System.out.println("2. Vehicle");
        System.out.println("3. Container");
        System.out.println("4. Manager");
        int choice = scanner.nextInt();

        scanner.nextLine();

        switch(choice) {
            case 1:
                System.out.println("Please enter the port name:");
                String newPort = scanner.nextLine();
                PORTS[PORTS.length - 1] = newPort;
                System.out.println("Port " + newPort + " added successfully.");
                break;
            case 2:
                System.out.println("Please enter the vehicle name:");
                String newVehicle = scanner.nextLine();
                VEHICLES[VEHICLES.length - 1] = newVehicle;
                System.out.println("Vehicle " + newVehicle + " added successfully.");
                break;
            case 3:
                if(isPortManager) {
                    System.out.println("Please enter the container name:");
                    String newContainer = scanner.nextLine();
                    CONTAINERS[CONTAINERS.length - 1] = newContainer;
                    System.out.println("Container " + newContainer + " added successfully.");
                }
                else {
                    System.out.println("Invalid choice. Please try again.");
                }
                break;
            case 4:
                if(isAdmin) {
                    System.out.println("Please enter the manager name:");
                    String newManager = scanner.nextLine();
                    System.out.println("Please choose the port for the new manager:");
                    for(int i=0; i<PORTS.length; i++) {
                        System.out.println((i+1) + ". " + PORTS[i]);
                    }
                    int portChoice = scanner.nextInt();
                    scanner.nextLine();
                    String selectedPort = PORTS[portChoice-1];
                    System.out.println("Manager " + newManager + " added to " + selectedPort + " successfully.");
                }
                else {
                    System.out.println("Invalid choice. Please try again.");
                }
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private static void removeEntity(Scanner scanner) {
        System.out.println("Please choose the entity type to remove:");
        System.out.println("1. Port");
        System.out.println("2. Vehicle");
        System.out.println("3. Container");
        System.out.println("4. Manager");
        int choice = scanner.nextInt();

        scanner.nextLine();

        switch(choice) {
            case 1:
                System.out.println("Please choose the port to remove:");
                for(int i=0; i<PORTS.length; i++) {
                    System.out.println((i+1) + ". " + PORTS[i]);
                }
                int portChoice = scanner.nextInt();
                scanner.nextLine();
                String removedPort = PORTS[portChoice-1];
                System.out.println("Port " + removedPort + " removed successfully.");
                // remove port from array
                String[] updatedPorts = new String[PORTS.length - 1];
                int updatedIndex = 0;
                for(int i=0; i<PORTS.length; i++) {
                    if(i != portChoice-1) {
                        updatedPorts[updatedIndex] = PORTS[i];
                        updatedIndex++;
                    }
                }
                PORTS = updatedPorts;
                break;
            case 2:
                System.out.println("Please choose the vehicle to remove:");
                for(int i=0; i<VEHICLES.length; i++) {
                    System.out.println((i+1) + ". " + VEHICLES[i]);
                }
                int vehicleChoice = scanner.nextInt();
                scanner.nextLine();
                String removedVehicle = VEHICLES[vehicleChoice-1];
                System.out.println("Vehicle " + removedVehicle + " removed successfully.");
                // remove vehicle from array
                String[] updatedVehicles = new String[VEHICLES.length - 1];
                int updatedVehicleIndex = 0;
                for(int i=0; i<VEHICLES.length; i++) {
                    if(i != vehicleChoice-1) {
                        updatedVehicles[updatedVehicleIndex] = VEHICLES[i];
                        updatedVehicleIndex++;
                    }
                }
                VEHICLES = updatedVehicles;
                break;
            case 3:
                if(isPortManager) {
                    System.out.println("Please choose the container to remove:");
                    for(int i=0; i<CONTAINERS.length; i++) {
                        System.out.println((i+1) + ". " + CONTAINERS[i]);
                    }
                    int containerChoice = scanner.nextInt();
                    scanner.nextLine();
                    String removedContainer = CONTAINERS[containerChoice-1];
                    System.out.println("Container " + removedContainer + " removed successfully.");
                    // remove container from array
                    String[] updatedContainers = new String[CONTAINERS.length - 1];
                    int updatedContainerIndex = 0;
                    for(int i=0; i<CONTAINERS.length; i++) {
                        if(i != containerChoice-1) {
                            updatedContainers[updatedContainerIndex] = CONTAINERS[i];
                            updatedContainerIndex++;
                        }
                    }
                    CONTAINERS = updatedContainers;
                }
                else {
                    System.out.println("Invalid choice. Please try again.");
                }
                break;
            case 4:
                if(isAdmin) {
                    System.out.println("Please choose the manager to remove:");
                    for(int i=0; i<PORTS.length; i++) {
                        System.out.println((i+1) + ". " + PORTS[i]);
                    }
                    int portChoiceToRemoveManager = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Please choose the manager to remove for port " + PORTS[portChoiceToRemoveManager-1] + ":");
                    for(int i=0; i<PORT_MANAGER_USERNAME.length(); i++) {
                        System.out.println((i+1) + ". " + PORT_MANAGER_USERNAME.charAt(i));
                    }
                    int managerChoice = scanner.nextInt();
                    scanner.nextLine();
                    // remove manager from port
                    System.out.println("Manager " + PORT_MANAGER_USERNAME.charAt(managerChoice-1) + " removed from " + PORTS[portChoiceToRemoveManager-1] + " successfully.");
                }
                else {
                    System.out.println("Invalid choice. Please try again.");
                }
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private static void viewInformation() {
        System.out.println("\n --- AVAILABLE INFORMATION ---");
        System.out.println("Ports:");
        for(String port : PORTS) {
            System.out.println("- " + port);
        }
        System.out.println("\nVehicles:");
        for(String vehicle : VEHICLES) {
            System.out.println("- " + vehicle);
        }
        System.out.println("\nContainers:");
        for(String container : CONTAINERS) {
            System.out.println("- " + container);
        }
        if(isAdmin) {
            System.out.println("\nPort Managers:");
            for(int i=0; i<PORT_MANAGER_USERNAME.length(); i++) {
                System.out.println("- " + PORT_MANAGER_USERNAME.charAt(i) + " (" + PORTS[i] + " port)");
            }
        }
    }

    private static void modifyInformation(Scanner scanner) {
        System.out.println("Please choose the entity type to modify:");
        System.out.println("1. Port");
        System.out.println("2. Vehicle");
        System.out.println("3. Container");
        System.out.println("4. Manager");
        int choice = scanner.nextInt();

        scanner.nextLine();

        switch(choice) {
            case 1:
                System.out.println("Please choose the port to modify:");
                for(int i=0; i<PORTS.length; i++) {
                    System.out.println((i+1) + ". " + PORTS[i]);
                }
                int portChoice = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Please enter the new name for port " + PORTS[portChoice-1] + ":");
                String updatedPort = scanner.nextLine();
                PORTS[portChoice-1] = updatedPort;
                System.out.println("Port updated successfully.");
                break;
            case 2:
                System.out.println("Please choose the vehicle to modify:");
                for(int i=0; i<VEHICLES.length; i++) {
                    System.out.println((i+1) + ". " + VEHICLES[i]);
                }
                int vehicleChoice = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Please enter the new name for vehicle " + VEHICLES[vehicleChoice-1] + ":");
                String updatedVehicle = scanner.nextLine();
                VEHICLES[vehicleChoice-1] = updatedVehicle;
                System.out.println("Vehicle updated successfully.");
                break;
            case 3:
                if(isPortManager) {
                    System.out.println("Please choose the container to modify:");
                    for(int i=0; i<CONTAINERS.length; i++) {
                        System.out.println((i+1) + ". " + CONTAINERS[i]);
                    }
                    int containerChoice = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Please enter the new name for container " + CONTAINERS[containerChoice-1] + ":");
                    String updatedContainer = scanner.nextLine();
                    CONTAINERS[containerChoice-1] = updatedContainer;
                    System.out.println("Container updated successfully.");
                }
                else {
                    System.out.println("Invalid choice. Please try again.");
                }
                break;
            case 4:
                if(isAdmin) {
                    System.out.println("Please choose the port to modify:");
                    for(int i=0; i<PORTS.length; i++) {
                        System.out.println((i+1) + ". " + PORTS[i]);
                    }
                    int portChoiceToModifyManager = scanner.nextInt();
                    System.out.println("Please choose the manager to modify for port " + PORTS[portChoiceToModifyManager-1] + ":");
                    for(int i=0; i<PORT_MANAGER_USERNAME.length(); i++) {
                        System.out.println((i+1) + ". " + PORT_MANAGER_USERNAME.charAt(i));
                    }
                    int managerChoice = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Please enter the new name for manager " + PORT_MANAGER_USERNAME.charAt(managerChoice-1) + ":");
                    String updatedManager = scanner.nextLine();
                    System.out.println("Manager " + PORT_MANAGER_USERNAME.charAt(managerChoice-1) + " in port " + PORTS[portChoiceToModifyManager-1] + " updated to " + updatedManager + " successfully.");
                }
                else {
                    System.out.println("Invalid choice. Please try again.");
                }
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private static void processContainers(Scanner scanner) {
        System.out.println("You are currently managing the " + currentPortManager + " port.");
        System.out.println("Please choose the container to process:");
        for(int i=0; i<CONTAINERS.length; i++) {
            System.out.println((i+1) + ". " + CONTAINERS[i]);
        }
        int containerChoice = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Container " + CONTAINERS[containerChoice-1] + " processed successfully.");
        // remove container from array
        String[] updatedContainers = new String[CONTAINERS.length - 1];
        int updatedContainerIndex = 0;
        for(int i=0; i<CONTAINERS.length; i++) {
            if(i != containerChoice-1) {
                updatedContainers[updatedContainerIndex] = CONTAINERS[i];
                updatedContainerIndex++;
            }
        }
        CONTAINERS = updatedContainers;
    }

}