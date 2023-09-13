package Ports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filename = "ports.txt";

        try {
            // Create a new port and save it to the file
            Ports port = new Ports("Port A", 10.0, 20.0, 3000.0, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            port.saveToFile(filename);
            System.out.println("Port created and saved to file: " + port);

            // Read all ports from the file
            List<Ports> portsList = Ports.readFromFile(filename);
            System.out.println("Ports read from file: " + portsList);

            // Create an updated port object to use in the updatePort method
            Ports updatedPort = new Ports("Updated Port A", 11.0, 22.0, 3500.0, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

            // Update a port (here updating the port created above)
            Ports.updatePort(filename, port.getId(), updatedPort, portsList);
            portsList = Ports.readFromFile(filename);  // Read again to verify update
            System.out.println("Ports after update: " + portsList);

            // Delete a port
            Ports.deletePort(filename, port.getId(), portsList);
            portsList = Ports.readFromFile(filename);  // Read again to verify deletion
            System.out.println("Ports after deletion: " + portsList);

        } catch (IOException e) {
            System.err.println("An IO exception occurred: " + e.getMessage());
        }
    }
}

