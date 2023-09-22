package container;

import ports.PortManagementSystem;

import java.io.*;
import java.util.*;

public class Menu {

    public static void displayMenu() {
        Scanner input = new Scanner(System.in).useDelimiter("\n");

        while (true) {
            try {
                ArrayList<String> arrayList = new ArrayList<>();

                System.out.println("Please choose your desired option");
                System.out.println("=================================");
                System.out.println("1. Create Container");
                System.out.println("2. Delete Container");
                System.out.println("3. Update Container");
                System.out.println("4. View Container by ID");
                System.out.println("5. View All Containers");
                System.out.println("6. Get Containers' Weight By Type");
                System.out.println("7. Exit");
                System.out.println("=================================");

                int option = -1;
                while (option < 1 || option > 7) {
                    try {
                        System.out.print("Choose a valid option (1-7): ");
                        option = input.nextInt();
                        if (option < 1 || option > 7) {
                            System.out.println("Invalid option. Please choose again.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a number between 1 and 7.");
                        input.next(); // clear the invalid input
                    }
                }

                switch (option) {
                    case 1 -> Container.createContainer(input);
                    case 2 -> Container.deleteContainer(arrayList, input);
                    case 3 -> Container.updateContainer(arrayList, input);
                    case 4 -> Container.viewContainerById(input);
                    case 5 -> Container.viewAllContainers();
                    case 6 -> Container.getTotalWeightByType();
                    case 7 -> {
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> {
                    }
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }
        }
    }


}
