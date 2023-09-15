package container;

import java.util.*;
import java.io.*;

public class Container {
    public static String filename = "containers.txt";

    public enum ContainerType {
        DRY_STORAGE(3.5, 4.6),
        OPEN_TOP(2.8, 3.2),
        OPEN_SIDE(2.7, 3.2),
        REFRIGERATED(4.5, 5.4),
        LIQUID(4.8, 5.3);

        private final double shipConsumption;
        private final double truckConsumption;

        ContainerType(double shipConsumption, double truckConsumption) {
            this.shipConsumption = shipConsumption;
            this.truckConsumption = truckConsumption;
        }

        public double getShipConsumption() {
            return shipConsumption;
        }

        public double getTruckConsumption() {
            return truckConsumption;
        }
    }

    private static int containerCounter;
    private static final HashMap<ContainerType, Double> totalWeightByType = new HashMap<>();
    private final String id;
    private final double weight;
    private final ContainerType type;

    static {
        containerCounter = getMaxContainerID();
    }

    public static int getMaxContainerID() {
        int maxID = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String idPart = parts[0];
                int idNumber = Integer.parseInt(idPart.split("-")[1]);
                if (idNumber > maxID) {
                    maxID = idNumber;
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
        return maxID;
    }

    public Container() {
        this(0.0, null);
    }

    private String generateContainerID() {
        return "c-" + (++containerCounter);
    }

    public Container(double weight, ContainerType type) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }
        this.id = generateContainerID();
        this.weight = weight;
        this.type = type;

        if(type != null){
            totalWeightByType.merge(type, weight, Double::sum);
        }
    }

    public String getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public ContainerType getType() {
        return type;
    }

    public double getFuelConsumptionPerKmForShip() {
        return type != null ? type.getShipConsumption() * weight : 0.0;
    }

    public double getFuelConsumptionPerKmForTruck() {
        return type != null ? type.getTruckConsumption() * weight : 0.0;
    }

    public static double getTotalWeight(ContainerType type) {
        return totalWeightByType.getOrDefault(type, 0.0);
    }

    public String getContainer() {
        return id + "," + weight + "," + type;
    }



    public static void createContainer(Scanner input) {
        System.out.print("Please enter the Container's weight: ");
        double weight = input.nextDouble();
        input.nextLine();

        Container.ContainerType type = null;
        while (type == null) {
            System.out.print("Please enter the Container's type (");
            System.out.print(Arrays.toString(Container.ContainerType.values()));
            System.out.print("): ");

            try {
                type = Container.ContainerType.valueOf(input.next().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid container type. Please try again.");
            }
        }

        Container container = new Container(weight, type);

        try {
            FileWriter writer = new FileWriter(filename, true);
            RandomAccessFile raf = new RandomAccessFile(filename, "r");
            long length = raf.length();
            if (length > 0) {
                raf.seek(length - 1);
                byte lastByte = raf.readByte();
                if (lastByte != 10 && lastByte != 13) {
                    writer.append("\n");
                }
            }
            writer.append(container.getContainer());
            // writer.append("\n");
            writer.close();
            raf.close();
            System.out.println("Container added successfully");
        } catch (IOException e) {
            System.out.println("Container not successfully added");
        }
    }

    public static void deleteContainer(ArrayList<String> arrayList, Scanner input) {
        while(true) {
            arrayList.clear();
            System.out.println("Enter the Container ID to delete a record: ");
            String searchKey = input.next();

            boolean isFound = false; // Variable to track if the ID was found

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(searchKey)) {
                        System.out.println("Deleting the following record: " + line);
                        isFound = true; // Set the variable to true as the ID is found
                    } else {
                        arrayList.add(line); // This should add all lines not containing the searchKey
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the file: " + e.getMessage());
            }

            if (!isFound) { // Check if the ID was not found and notify the user
                System.out.println("No record found with the ID: " + searchKey);
                System.out.println("Please try again.");
                continue; // Repeat the loop to prompt the user again
            } else {
                try (FileWriter writer = new FileWriter(filename)) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        writer.write(arrayList.get(i));
                        if (i < arrayList.size() - 1) { // Prevent writing a newline character at the end of the file
                            writer.write("\n");
                        }
                    }
                    System.out.println("Deletion successful!");
                    break; // Break out of the while loop as the operation is successful
                } catch (IOException e) {
                    System.out.println("An error occurred while writing to the file: " + e.getMessage());
                }
            }
        }
    }

    public static void updateContainer(ArrayList<String> arrayList, Scanner input) {
        arrayList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            System.out.println("Please enter the Container ID to update the record:");
            String searchKey = input.next();
            String line;

            boolean found = false;
            while ((line = reader.readLine()) != null) {
                arrayList.add(line);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).contains(searchKey)) {
                    found = true;
                    System.out.println("Found record: " + arrayList.get(i));

                    String fieldToUpdate;
                    while (true) {
                        System.out.println("What do you want to update? (weight/type):");
                        fieldToUpdate = input.next().toLowerCase();

                        if (fieldToUpdate.equals("weight") || fieldToUpdate.equals("type")) {
                            break;
                        } else {
                            System.out.println("Invalid field. Please enter 'weight' or 'type'.");
                        }
                    }

                    System.out.println("Enter the new value:");
                    String newValue = input.next();
                    String[] lineParts = arrayList.get(i).split(",");

                    switch (fieldToUpdate) {
                        case "weight" -> lineParts[1] = newValue;
                        case "type" -> lineParts[2] = newValue.toUpperCase();
                    }

                    arrayList.set(i, String.join(",", lineParts));
                    System.out.println("Record updated: " + arrayList.get(i));
                    break;
                }
            }
            if (!found) {
                System.out.println("No record found with the given ID.");
            }

        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

        try (FileWriter writer = new FileWriter(filename)) {
            for (String record : arrayList) {
                writer.write(record);
                writer.write("\n");
            }
            System.out.println("Update successful!");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public static void viewContainerById(Scanner input) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            System.out.println("Please enter the Container ID to view the information:");
            String searchKey = input.next();
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (line.contains(searchKey)) {
                    System.out.println(line);
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("No container found with the ID: " + searchKey);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
    }

    public static void viewAllContainers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            System.out.println("Listing all containers:");

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            System.out.println("End of list.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
    }
}
