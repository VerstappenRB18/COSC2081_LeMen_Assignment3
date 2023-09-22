package container;

import ports.PortManagementSystem;

import java.io.*;
import java.util.*;

public class Menu {

// main method
    public static void main(String[] args) throws IOException {
        System.out.println("COSC2081 GROUP ASSIGNMENT");
        System.out.println("CONTAINER PORT MANAGEMENT SYSTEM");
        System.out.println("Instructor: Mr. Minh Vu & Dr. Phong Ngo");
        System.out.println("Group: LeMen");
        System.out.println("s3986287, Nguyen Vinh Gia Bao");
//        displayMenu();
        String[] portManagementArgs = {};
        PortManagementSystem.main(portManagementArgs);
    }


    public static void displayMenu() {
        Scanner input = new Scanner(System.in).useDelimiter("\n");

        while(true) {
            ArrayList<String> arrayList = new ArrayList<>();

            System.out.println("Please choose your desired option");
            System.out.println("=================================");
            System.out.println("1. Container");
            System.out.println("2. Vehicle");
            System.out.println("3. Trip");
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
        }
   }

}
