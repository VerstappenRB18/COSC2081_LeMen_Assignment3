package trip;

import container.Container;
import ports.Ports;
import vehicle.Vehicle;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Trip {
    private static int tripCounter;
    private String id;
    private Vehicle vehicle;
    private Ports departurePort;
    private Ports arrivalPort;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private static TripStatus status;
    private List<Container> containers;

    public static final String filename = "trips.csv";

    static {
        tripCounter = getMaxTripID();
    }

    public Trip() {
        this.id = null;
        this.vehicle = null;
        this.departurePort = null;
        this.arrivalPort = null;
        this.departureDate = null;
        this.arrivalDate = null;
        status = null;
        this.containers = new ArrayList<>();
    }

    public Trip(Vehicle vehicle, Ports departurePort, Ports arrivalPort, LocalDateTime departureDate, LocalDateTime arrivalDate, TripStatus status) {
        this.id = generateTripID();
        this.vehicle = vehicle;
        this.departurePort = departurePort;
        this.arrivalPort = arrivalPort;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        Trip.status = status;
    }

    public static int getMaxTripID() {
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

    private String generateTripID() {
        return "t-" + (++tripCounter);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Ports getDeparturePort() {
        return departurePort;
    }

    public void setDeparturePort(Ports departurePort) {
        this.departurePort = departurePort;
    }

    public Ports getArrivalPort() {
        return arrivalPort;
    }

    public void setArrivalPort(Ports arrivalPort) {
        this.arrivalPort = arrivalPort;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        Trip.status = status;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static void createTrip(List<Trip> tripList, List<Vehicle> vehicleList, List<Ports> portsList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter vehicle ID: ");
        String vehicleId = reader.readLine();
        Vehicle vehicle = findVehicleById(vehicleList, vehicleId);
        if (vehicle == null) {
            System.out.println("Invalid vehicle ID.");
            return;
        }

        Ports departurePort = vehicle.getCurrentPort();
        System.out.println("Default departure port is: " + departurePort.getId() + ". Confirm? (yes/no)");
        String confirm = reader.readLine();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Trip creation cancelled.");
            return;
        }

        List<Ports> availablePorts = new ArrayList<>();
        for (Ports port : portsList) {
            double distance = departurePort.calculateDistance(port);
            if (vehicle.canMoveToPort(departurePort, port) && vehicle.hasEnoughFuel(distance) && port.isLandingAbility()) {
                availablePorts.add(port);
            }
        }

        if (availablePorts.isEmpty()) {
            System.out.println("No available ports.");
            return;
        }

        for (int i = 0; i < availablePorts.size(); i++) {
            System.out.println((i + 1) + ". " + availablePorts.get(i).getId());
        }

        System.out.print("Select arrival port by ID: ");
        String selectedPortId = reader.readLine();
        Ports arrivalPort = findPortById(availablePorts, selectedPortId);

        if (arrivalPort == null) {
            System.out.println("Invalid arrival port ID.");
            return;
        }

        LocalDateTime departureDate = null;
        while (departureDate == null) {
            try {
                System.out.print("Enter departure date (yyyy-MM-dd HH:mm): ");
                String departureDateString = reader.readLine();
                departureDate = LocalDateTime.parse(departureDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }

        LocalDateTime arrivalDate = null;
        while (arrivalDate == null) {
            try {
                System.out.print("Enter arrival date (yyyy-MM-dd HH:mm): ");
                String arrivalDateString = reader.readLine();
                arrivalDate = LocalDateTime.parse(arrivalDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }

        TripStatus status = null;
        while (status == null) {
            try {
                System.out.print("Enter trip status (PLANNED/IN_PROGRESS/COMPLETED/CANCELLED): ");
                String statusString = reader.readLine();
                status = TripStatus.valueOf(statusString.toUpperCase());
            } catch (Exception e) {
                System.out.println("Invalid status.");
            }
        }
        tripCounter = getMaxTripID();

        Trip trip = new Trip(vehicle, departurePort, arrivalPort, departureDate, arrivalDate, status);
        tripList.add(trip);

        // Save the trip to trips.csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.newLine();  // Move to the next line before writing the new entry
            String tripData = String.join(",",
                    trip.getId(),
                    trip.getVehicle().getId(),
                    trip.getDeparturePort().getId(),
                    trip.getArrivalPort().getId(),
                    trip.getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    trip.getArrivalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    trip.getStatus().name()
            );
            writer.write(tripData);
        }
        catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }

        Trip latestTrip = findLatestCompletedTripForVehicle(tripList, vehicle);
        if (latestTrip == null || latestTrip.getArrivalDate().isBefore(arrivalDate)) {
            vehicle.setCurrentPort(arrivalPort);
        }
    }

    public static void updateTrip(List<Trip> tripList, List<Vehicle> vehicleList, List<Ports> portsList, String filename) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter trip ID to update: ");
        String tripId = scanner.nextLine();
        Trip tripToUpdate = findTripById(tripList, tripId);
        if (tripToUpdate == null) {
            System.out.println("Invalid trip ID.");
            return;
        }
        System.out.print("Enter new vehicle ID: ");
        String vehicleId = scanner.nextLine();
        Vehicle vehicle = findVehicleById(vehicleList, vehicleId);
        if (vehicle != null) {
            tripToUpdate.setVehicle(vehicle);
        }
        System.out.print("Enter new departure port ID: ");
        String departurePortId = scanner.nextLine();
        Ports newDeparturePort = findPortById(portsList, departurePortId);
        if (newDeparturePort != null) {
            tripToUpdate.setDeparturePort(newDeparturePort);
        }
        System.out.print("Enter new arrival port ID: ");
        String arrivalPortId = scanner.nextLine();
        Ports newArrivalPort = findPortById(portsList, arrivalPortId);
        if (newArrivalPort != null) {
            tripToUpdate.setArrivalPort(newArrivalPort);
        }
        System.out.print("Enter new departure date (yyyy-MM-dd HH:mm): ");
        String departureDateInput = scanner.nextLine();
        try {
            LocalDateTime newDepartureDate = LocalDateTime.parse(departureDateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            tripToUpdate.setDepartureDate(newDepartureDate);
        } catch (Exception e) {
            System.out.println("Invalid date format.");
        }
        System.out.print("Enter new arrival date (yyyy-MM-dd HH:mm): ");
        String arrivalDateInput = scanner.nextLine();
        try {
            LocalDateTime newArrivalDate = LocalDateTime.parse(arrivalDateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            tripToUpdate.setArrivalDate(newArrivalDate);
        } catch (Exception e) {
            System.out.println("Invalid date format.");
        }
        System.out.print("Enter new status (PLANNED/IN_PROGRESS/COMPLETED/CANCELLED): ");
        String statusInput = scanner.nextLine();
        try {
            TripStatus newStatus = TripStatus.valueOf(statusInput.toUpperCase());
            tripToUpdate.setStatus(newStatus);
        } catch (Exception e) {
            System.out.println("Invalid status.");
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader1 = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader1.readLine()) != null) {
                lines.add(line);
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            if (parts[0].equals(tripId)) {
                String updatedTripData = String.join(",",
                        tripToUpdate.getId(),
                        tripToUpdate.getVehicle().getId(),
                        tripToUpdate.getDeparturePort().getId(),
                        tripToUpdate.getArrivalPort().getId(),
                        tripToUpdate.getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        tripToUpdate.getArrivalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        tripToUpdate.getStatus().name()
                );
                lines.set(i, updatedTripData);
                break;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) {
                    writer.newLine();
                }
            }
        }

        Trip latestTrip = findLatestCompletedTripForVehicle(tripList, vehicle);
        if (latestTrip == null || latestTrip.getArrivalDate().isBefore(tripToUpdate.getArrivalDate())) {
            assert vehicle != null;
            vehicle.setCurrentPort(newArrivalPort);
        }
    }

    public static void viewAllTrips(List<Trip> tripList, List<Vehicle> vehicleList) {
        for (Trip trip : tripList) {
            Vehicle tripVehicle = trip.getVehicle();
            List<Container> tripContainers = trip.getContainers();
            List<Container> actualLoadedContainers = new ArrayList<>();

            // Find the corresponding vehicle from the vehicle list
            Vehicle actualVehicle = findVehicleById(vehicleList, tripVehicle.getId());

            if (actualVehicle != null) {
                // Get the containers actually loaded on the vehicle
                List<Container> vehicleContainers = actualVehicle.getContainers();

                // Filter only the containers that are actually loaded on the vehicle for this trip
                for (Container c : tripContainers) {
                    if (vehicleContainers.contains(c)) {
                        actualLoadedContainers.add(c);
                    }
                }
            }

            // Display the trip information
            System.out.println("Trip Details: \n" +
                    "-------------------------\n" +
                    "Vehicle ID: " + tripVehicle.getId() + "\n" +
                    "Departure Port: " + trip.getDeparturePort().getId() + "\n" +
                    "Arrival Port: " + trip.getArrivalPort().getId() + "\n" +
                    "Departure Date: " + trip.getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                    "Arrival Date: " + trip.getArrivalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n" +
                    "Trip Status: " + trip.getStatus() + "\n" +
                    "Containers: " + actualLoadedContainers.stream().map(Container::getId).collect(Collectors.joining(", ")) + "\n" +
                    "-------------------------");
        }
    }


    public static void deleteTrip(List<Trip> tripList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter trip ID to delete: ");
        String tripId = reader.readLine();

        // Remove the trip from the tripList
        Trip tripToRemove = null;
        for (Trip trip : tripList) {
            if (trip.getId().equals(tripId)) {
                tripToRemove = trip;
                break;
            }
        }
        if (tripToRemove == null) {
            System.out.println("Invalid trip ID.");
            return;
        }
        tripList.remove(tripToRemove);


        List<String> lines = new ArrayList<>();
        try (BufferedReader csvReader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = csvReader.readLine()) != null) {
                if (!line.startsWith(tripId + ",")) {
                    lines.add(line);
                }
            }
        }

        try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < lines.size(); i++) {
                csvWriter.write(lines.get(i));
                if (i < lines.size() - 1) {
                    csvWriter.newLine();
                }
            }
        }
    }


    public static void listTripsOnGivenDay(List<Trip> tripList) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the day you want to view trips: ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = scanner.nextLine();
        LocalDate givenDate = LocalDate.parse(dateStr, formatter);

        boolean tripFound = false;  // Flag to keep track of whether any trips are found

        for (Trip trip : tripList) {
            LocalDate departureDate = trip.getDepartureDate().toLocalDate();
            LocalDate arrivalDate = trip.getArrivalDate().toLocalDate();
            if (givenDate.equals(departureDate) || givenDate.equals(arrivalDate)) {
                System.out.println(trip);
                tripFound = true;  // Set the flag to true because a trip was found
            }
        }

        // Check the flag to print appropriate message
        if (!tripFound) {
            System.out.println("No trips were found for the given date: " + givenDate);
        }
    }


    public static void listTripsFromDayAToDayB(List<Trip> tripList) throws IOException {
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.print("Please enter Day A: ");
        String startDateStr = scanner.nextLine();
        System.out.print("Please enter Day B: ");
        String endDateStr = scanner.nextLine();
        LocalDate start = LocalDate.parse(startDateStr, formatter);
        LocalDate end = LocalDate.parse(endDateStr, formatter);

        boolean tripFound = false; // Flag to keep track of whether any trips are found

        for (Trip trip : tripList) {
            LocalDate departureDate = trip.getDepartureDate().toLocalDate();
            LocalDate arrivalDate = trip.getArrivalDate().toLocalDate();
            if ((departureDate.isAfter(start) || departureDate.equals(start)) && (arrivalDate.isBefore(end) || arrivalDate.equals(end))) {
                System.out.println(trip);
                tripFound = true; // Set the flag to true because a trip was found
            }
        }

        // Check the flag to print an appropriate message
        if (!tripFound) {
            System.out.println("No trips were found between " + start + " and " + end);
        }
    }



    public static Vehicle findVehicleById(List<Vehicle> vehicleList, String vehicleId) {
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getId().equals(vehicleId)) {
                return vehicle;
            }
        }
        return null;
    }

    public static Ports findPortById(List<Ports> portsList, String portId) {
        return portsList.stream()
                .filter(port -> port.getId().equals(portId))
                .findFirst()
                .orElse(null);
    }

    public static Trip findTripById(List<Trip> tripList, String tripId) {
        return tripList.stream()
                .filter(trip -> trip.getId().equals(tripId))
                .findFirst()
                .orElse(null);
    }

    public static List<Trip> readFromFile(String filename, List<Vehicle> vehicleList, List<Ports> portsList) throws IOException {
        List<Trip> tripList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                String tripId = details[0];  // Parsing tripID at index 0
                Vehicle vehicle = findVehicleById(vehicleList, details[1]);
                Ports departurePort = findPortById(portsList, details[2]);
                Ports arrivalPort = findPortById(portsList, details[3]);
                LocalDateTime departureDate = LocalDateTime.parse(details[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime arrivalDate = LocalDateTime.parse(details[5], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                TripStatus status = TripStatus.valueOf(details[6]);
                Trip trip = new Trip(vehicle, departurePort, arrivalPort, departureDate, arrivalDate, status);
                trip.setId(tripId);
                tripList.add(trip);
            }
        }
        return tripList;
    }


    public static Trip findLatestCompletedTripForVehicle(List<Trip> tripList, Vehicle vehicle) {
        Trip latestTrip = null;
        for (Trip trip : tripList) {
            if (trip.getVehicle().equals(vehicle) && trip.getStatus() == TripStatus.COMPLETED) {
                if (latestTrip == null || trip.getArrivalDate().isAfter(latestTrip.getArrivalDate())) {
                    latestTrip = trip;
                }
            }
        }
        return latestTrip;
    }


    public enum TripStatus {
        PLANNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Trip Information:\n");
        sb.append("  - Vehicle ID: ").append(vehicle != null ? vehicle.getId() : "N/A").append('\n');  // Check if vehicle is null
        sb.append("  - Departure Port: ").append(departurePort.getId()).append('\n');
        sb.append("  - Arrival Port: ").append(arrivalPort.getId()).append('\n');
        sb.append("  - Departure Date: ").append(departureDate).append('\n');
        sb.append("  - Arrival Date: ").append(arrivalDate).append('\n');
        sb.append("  - Status: ").append(status).append('\n');
        sb.append("  - Containers: ");

        if (containers != null) {  // Check if containers is null before iterating
            for (Container container : containers) {
                sb.append(container != null ? container.getId() : "N/A").append(", ");  // Check if container is null
            }

            // Remove the trailing comma and space
            if (!containers.isEmpty()) {
                sb.setLength(sb.length() - 2);
            }
        } else {
            sb.append("N/A");
        }

        return sb.toString();
    }


}
