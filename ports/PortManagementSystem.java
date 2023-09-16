package ports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

public class PortManagementSystem {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        List<Ports> portsList = Ports.readFromFile(Ports.filename);

        while (true) {
            System.out.println("Please select an option:");
            System.out.println("1. Create new port");
            System.out.println("2. View all ports");
            System.out.println("3. Update existing port");
            System.out.println("4. Delete existing port");
            System.out.println("5. Exit");

            String choice = reader.readLine();

            switch (choice) {
                case "1":
                    Ports.createPort(portsList, Ports.filename);
                    break;
                case "2":
                    for (Ports port : portsList) {
                        System.out.println(port);
                    }
                    break;
                case "3":
                    Ports.updatePort(portsList, Ports.filename);
                    break;
                case "4":
                    Ports.deletePort(portsList, Ports.filename);
                    break;
                case "5":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

}
