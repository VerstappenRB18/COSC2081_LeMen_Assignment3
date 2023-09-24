package container;

import ports.Ports;

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
    private final String portId;

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

    public Container(String id, double weight, ContainerType type, String portId) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }
        this.id = id;
        this.weight = weight;
        this.type = type;
        this.portId = portId;

        if (type != null) {
            totalWeightByType.merge(type, weight, Double::sum);
        }
    }

    private String generateContainerID() {
        return "c-" + (++containerCounter);
    }

    public Container(double weight, ContainerType type, String portId) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }
        this.id = generateContainerID();
        this.weight = weight;
        this.type = type;
        this.portId = portId;

        if (type != null) {
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

    public String getPortId() {
        return portId;
    }

    public double getFuelConsumptionPerKmForShip() {
        return type != null ? type.getShipConsumption() * weight : 0.0;
    }

    public double getFuelConsumptionPerKmForTruck() {
        return type != null ? type.getTruckConsumption() * weight : 0.0;
    }

    public String getContainer() {
        return id + "," + weight + "," + type + "," + portId;
    }

    public static void getTotalWeightByType() {
        totalWeightByType.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String typeString = parts[2];
                    if (typeString == null || typeString.isBlank()) {
                        System.out.println("Container type is null or blank on line: " + line);
                        continue;
                    }
                    try {
                        ContainerType type = ContainerType.valueOf(typeString);
                        double weight = Double.parseDouble(parts[1]);
                        totalWeightByType.merge(type, weight, Double::sum);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid data on line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

        for (Map.Entry<ContainerType, Double> entry : totalWeightByType.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public static void createContainer(Scanner input) {
        System.out.print("Please enter the Container's weight: ");
        double weight = input.nextDouble();
        input.nextLine();

        Container.ContainerType type = null;
        while (type == null) {
            System.out.print("Please enter the Container's type: ");
            try {
                type = Container.ContainerType.valueOf(input.next().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid container type. Please try again.");
            }
        }

        System.out.print("Please enter the Container's port ID: ");
        String portId = input.next();

        Container container = new Container(weight, type, portId);

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
            writer.close();
            raf.close();
            System.out.println("Container added successfully");
        } catch (IOException e) {
            System.out.println("Container not successfully added");
        }
    }

    public static void deleteContainer(ArrayList<String> arrayList, Scanner input) {
        while (true) {
            arrayList.clear();
            System.out.println("Enter the Container ID to delete a record: ");
            String searchKey = input.next();

            boolean isFound = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(searchKey)) {
                        System.out.println("Deleting the following record: " + line);
                        isFound = true;
                    } else {
                        arrayList.add(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while reading the file: " + e.getMessage());
            }

            if (!isFound) {
                System.out.println("No record found with the ID: " + searchKey);
                System.out.println("Please try again.");
            } else {
                try (FileWriter writer = new FileWriter(filename)) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        writer.write(arrayList.get(i));
                        if (i < arrayList.size() - 1) {
                            writer.write("\n");
                        }
                    }
                    System.out.println("Deletion successful!");
                    break;
                } catch (IOException e) {
                    System.out.println("An error occurred while writing to the file: " + e.getMessage());
                }
            }
        }
    }

    public static void updateContainer(ArrayList<String> arrayList, Scanner input) {
        arrayList.clear();
        String searchKey;
        while (true) {
            System.out.print("Please enter the Container ID to update the record (format: c-number): ");
            searchKey = input.next();
            if (searchKey.matches("c-\\d+")) {
                break;
            } else {
                System.out.println("Invalid format. Please enter the Container ID in the format 'c-number'.");
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            System.out.println("Reading from file: " + filename);
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
                        System.out.print("What do you want to update? (weight/type/portid): ");
                        fieldToUpdate = input.next().toLowerCase();
                        if (fieldToUpdate.equals("weight") || fieldToUpdate.equals("type") || fieldToUpdate.equals("portid")) {
                            break;
                        } else {
                            System.out.println("Invalid field. Please enter 'weight', 'type', or 'portid'.");
                        }
                    }

                    String newValue;  // Initialize to null
                    String[] lineParts = arrayList.get(i).split(",");

                    switch (fieldToUpdate) {
                        case "weight":
                            while (true) {
                                try {
                                    Ports relevantPort = Ports.findPortById(lineParts[3]); // portId is at index 3
                                    if (relevantPort != null) {
                                        double storingCapacity = relevantPort.getStoringCapacity();
                                        System.out.println("The port's remaining storage capacity is: " + storingCapacity);

                                        System.out.print("Enter the new weight: ");
                                        newValue = input.next();  // Get newValue only for this case

                                        int newIntValue = Integer.parseInt(newValue);
                                        String formattedValue = String.format("%.1f", (double) newIntValue);

                                        if (Double.parseDouble(formattedValue) <= storingCapacity) {
                                            lineParts[1] = formattedValue;
                                            break; // Exit the loop as the weight is within capacity
                                        } else {
                                            System.out.println("The updated weight exceeds the port's remaining storage capacity. Please try again.");
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input for weight. Please enter an integer.");
                                }
                            }
                            break;
                        case "type":
                            System.out.print("Enter the new container's type: ");
                            newValue = input.next();
                            lineParts[2] = newValue.toUpperCase();

                            List<String> vehicleLines = new ArrayList<>();
                            try (BufferedReader vehicleReader = new BufferedReader(new FileReader("vehicles.csv"))) {
                                String vehicleLine;
                                while ((vehicleLine = vehicleReader.readLine()) != null) {
                                    vehicleLines.add(vehicleLine);
                                }
                            } catch (IOException e) {
                                System.out.println("An error occurred while reading vehicles.csv: " + e.getMessage());
                            }

                            for (int j = 0; j < vehicleLines.size(); j++) {
                                String[] vehicleParts = vehicleLines.get(j).split(",");
                                if (vehicleParts.length > 8 && vehicleParts[8].equals(searchKey)) {
                                    String vehicleType = vehicleParts[6];
                                    if ("Truck".equals(vehicleType)) {
                                        if (!isValidVehicleForContainer(vehicleParts[7], newValue)) {
                                            vehicleParts[8] = "";
                                            vehicleLines.set(j, String.join(",", vehicleParts));
                                            System.out.println("Removed container from incompatible truck.");
                                        }
                                    }
                                    break;
                                }
                            }


                            try (BufferedWriter vehicleWriter = new BufferedWriter(new FileWriter("vehicles.csv"))) {
                                for (int j = 0; j < vehicleLines.size(); j++) {
                                    vehicleWriter.write(vehicleLines.get(j));
                                    if (j < vehicleLines.size() - 1) {
                                        vehicleWriter.newLine();
                                    }
                                }
                            } catch (IOException e) {
                                System.out.println("An error occurred while writing to vehicles.csv: " + e.getMessage());
                            }
                            break;
                        case "portid":
                            System.out.print("Enter the new portId: ");
                            newValue = input.next();  // Get newValue for this case
                            lineParts[3] = newValue;
                            break;
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
            for (int i = 0; i < arrayList.size(); i++) {
                writer.write(arrayList.get(i));
                if (i < arrayList.size() - 1) {
                    writer.write("\n");
                }
            }
            System.out.println("Update successful!");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public static boolean isValidVehicleForContainer(String vehicleType, String containerType) {
        return switch (vehicleType.toUpperCase()) {
            case "BASIC" -> Arrays.asList("DRY_STORAGE", "OPEN_TOP", "OPEN_SIDE").contains(containerType.toUpperCase());
            case "REEFER" -> "REFRIGERATED".equalsIgnoreCase(containerType);
            case "TANKER" -> "LIQUID".equalsIgnoreCase(containerType);
            default -> false;
        };
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

    public static List<Container> readFromFile(String filename) throws IOException {
        List<Container> containerList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String id = parts[0];
                double weight = Double.parseDouble(parts[1]);
                ContainerType type = ContainerType.valueOf(parts[2]);
                String portId = parts[3];
                Container container = new Container(id, weight, type, portId);
                containerList.add(container);
            }
        }
        return containerList;
    }





    @Override
    public String toString() {
        return "Container{" +
                "id='" + id + '\'' +
                ", weight=" + weight +
                ", type=" + type +
                ", portId='" + portId + '\'' +
                '}';
    }
}
