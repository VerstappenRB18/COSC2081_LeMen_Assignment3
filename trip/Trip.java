package trip;

import ports.Ports;
import vehicle.Vehicle;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Trip {
    private Vehicle vehicle;
    private Ports departurePort;
    private Ports arrivalPort;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private TripStatus status;

    public static final String filename = "trips.csv";

    // Default constructor
    public Trip(Vehicle vehicle, Ports currentPort, Ports targetPort, String departureDate, String arrivalDate, TripStatus completed) {
        this.vehicle = null;
        this.departurePort = null;
        this.arrivalPort = null;
        this.departureDate = null;
        this.arrivalDate = null;
        this.status = null;
    }

    // Parameterized constructor
    public Trip(Vehicle vehicle, Ports departurePort, Ports arrivalPort, LocalDateTime departureDate, LocalDateTime arrivalDate, TripStatus status) {
        this.vehicle = vehicle;
        this.departurePort = departurePort;
        this.arrivalPort = arrivalPort;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.status = status;
    }

    // Getters and setters
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
        this.status = status;
    }
    // Method to create and add a new trip to the list and save it to the file
    public static void createTrip(List<Trip> tripList, List<Vehicle> vehicleList, List<Ports> portsList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Available vehicles:");
        for (int i = 0; i < vehicleList.size(); i++) {
            System.out.println((i + 1) + ". " + vehicleList.get(i).getId());
        }
        System.out.print("Select a vehicle by entering its number: ");
        int vehicleIndex = Integer.parseInt(reader.readLine()) - 1;
        Vehicle vehicle = vehicleList.get(vehicleIndex);

        System.out.println("Available departure ports:");
        for (int i = 0; i < portsList.size(); i++) {
            System.out.println((i + 1) + ". " + portsList.get(i).getId());
        }
        System.out.print("Select a departure port by entering its number: ");
        int departurePortIndex = Integer.parseInt(reader.readLine()) - 1;
        Ports departurePort = portsList.get(departurePortIndex);

        System.out.println("Available arrival ports:");
        for (int i = 0; i < portsList.size(); i++) {
            if (i != departurePortIndex) {
                System.out.println((i + 1) + ". " + portsList.get(i).getId());
            }
        }
        System.out.print("Select an arrival port by entering its number: ");
        int arrivalPortIndex = Integer.parseInt(reader.readLine()) - 1;
        Ports arrivalPort = portsList.get(arrivalPortIndex);

        double distance = departurePort.calculateDistance(arrivalPort);
        boolean canMoveToPort = vehicle.canMoveToPort(departurePort, arrivalPort);
        boolean hasEnoughFuel = vehicle.hasEnoughFuel(distance);

        System.out.println("Debug Info:");
        System.out.println("Distance: " + distance);
        System.out.println("Can move to port: " + canMoveToPort);
        System.out.println("Has enough fuel: " + hasEnoughFuel);
        double calculateFuelConsumption = vehicle.calculateFuelConsumption(distance);
        System.out.println("Calculated fuel consumption " + calculateFuelConsumption);

        if (canMoveToPort && hasEnoughFuel) {
            LocalDateTime departureDate = null;
            while(departureDate == null) {
                try {
                    System.out.print("Enter departure date (yyyy-MM-dd HH:mm): ");
                    String departureDateString = reader.readLine();
                    departureDate = LocalDateTime.parse(departureDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please try again.");
                }
            }

            LocalDateTime arrivalDate = null;
            while(arrivalDate == null) {
                try {
                    System.out.print("Enter arrival date (yyyy-MM-dd HH:mm): ");
                    String arrivalDateString = reader.readLine();
                    arrivalDate = LocalDateTime.parse(arrivalDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please try again.");
                }
            }

            TripStatus status = null;
            while(status == null) {
                try {
                    System.out.print("Enter status (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED): ");
                    String statusString = reader.readLine();
                    status = TripStatus.valueOf(statusString.toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid status. Please try again.");
                }
            }

            Trip trip = new Trip(vehicle, departurePort, arrivalPort, departureDate, arrivalDate, status);
            tripList.add(trip);
            trip.saveToFile(filename, vehicleList, portsList);

            System.out.println("Trip created successfully.");
        } else {
            if (!canMoveToPort) {
                System.out.println("The selected vehicle cannot move to the chosen arrival port.");
            }
            if (!hasEnoughFuel) {
                System.out.println("The selected vehicle does not have enough fuel for the journey.");
            }
        }
    }




    // Method to view all trips
    public static void viewAllTrips(List<Trip> tripList) {
        for (Trip trip : tripList) {
            System.out.println(trip);
        }
    }

    // Method to update an existing trip
    public static void updateTrip(List<Trip> tripList, List<Vehicle> vehicleList, List<Ports> portsList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter trip index to update: ");
        int index = Integer.parseInt(reader.readLine());

        if (index >= 0 && index < tripList.size()) {
            Trip tripToUpdate = tripList.get(index);

            // Update vehicle
            System.out.println("Available vehicles:");
            for (int i = 0; i < vehicleList.size(); i++) {
                System.out.println((i + 1) + ". " + vehicleList.get(i).getId()); // Assuming Vehicle class has getId() method
            }
            System.out.print("Select a new vehicle by entering its number (or press Enter to keep the current vehicle): ");
            String vehicleInput = reader.readLine();
            if (!vehicleInput.isEmpty()) {
                int vehicleIndex = Integer.parseInt(vehicleInput) - 1;
                tripToUpdate.setVehicle(vehicleList.get(vehicleIndex));
            }

            // Update departure port
            // Update departure port
            System.out.println("Available departure ports:");
            for (int i = 0; i < portsList.size(); i++) {
                System.out.println((i + 1) + ". " + portsList.get(i).getId()); // Assuming Ports class has getId() method
            }
            System.out.print("Select a new departure port by entering its number (or press Enter to keep the current port): ");
            String departurePortInput = reader.readLine();
            if (!departurePortInput.isEmpty()) {
                int departurePortIndex = Integer.parseInt(departurePortInput) - 1;
                tripToUpdate.setDeparturePort(portsList.get(departurePortIndex));
            }

            System.out.println("Available arrival ports:");
            for (int i = 0; i < portsList.size(); i++) {
                if (departurePortInput.isEmpty() || i != (Integer.parseInt(departurePortInput) - 1)) { // Exclude the selected departure port
                    System.out.println((i + 1) + ". " + portsList.get(i).getId());
                }
            }
            System.out.print("Select a new arrival port by entering its number (or press Enter to keep the current port): ");
            String arrivalPortInput = reader.readLine();
            if (!arrivalPortInput.isEmpty()) {
                int arrivalPortIndex = Integer.parseInt(arrivalPortInput) - 1;
                tripToUpdate.setArrivalPort(portsList.get(arrivalPortIndex));
            }


            // Update departure date
            System.out.print("Enter a new departure date in the format yyyy-MM-dd HH:mm (or press Enter to keep the current date): ");
            String departureDateInput = reader.readLine();
            if (!departureDateInput.isEmpty()) {
                LocalDateTime newDepartureDate = LocalDateTime.parse(departureDateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                tripToUpdate.setDepartureDate(newDepartureDate);
            }

            // Update arrival date
            System.out.print("Enter a new arrival date in the format yyyy-MM-dd HH:mm (or press Enter to keep the current date): ");
            String arrivalDateInput = reader.readLine();
            if (!arrivalDateInput.isEmpty()) {
                LocalDateTime newArrivalDate = LocalDateTime.parse(arrivalDateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                tripToUpdate.setArrivalDate(newArrivalDate);
            }

            // Update status
            System.out.print("Enter a new status (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED) or press Enter to keep the current status: ");
            String statusInput = reader.readLine();
            if (!statusInput.isEmpty()) {
                TripStatus newStatus = TripStatus.valueOf(statusInput.toUpperCase());
                tripToUpdate.setStatus(newStatus);
            }

            saveAllToFile(filename, tripList);
            System.out.println("Trip updated successfully.");
        } else {
            System.out.println("Invalid index. Please try again.");
        }
    }


    // Method to delete a trip
    public static void deleteTrip(List<Trip> tripList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter trip index to delete: ");
        int index = Integer.parseInt(reader.readLine());

        tripList.remove(index);
        saveAllToFile(filename, tripList);
        System.out.println("Trip deleted successfully.");
    }

    // Method to save a trip to a file
    public void saveToFile(String filename, List<Vehicle> vehicleList, List<Ports> portsList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            writer.write(String.format("%s,%s,%s,%s,%s,%s%n", vehicle.getId(), departurePort.getId(), arrivalPort.getId(), departureDate.format(formatter), arrivalDate.format(formatter), status));
        }

        // Get the latest list of trips and update vehicle ports
        List<Trip> tripList = readFromFile(filename, vehicleList, portsList);
}

    public static void updateVehiclePortsAfterTripCompletion(List<Trip> tripList, List<Vehicle> vehicleList) {
        for (Trip trip : tripList) {
            if (trip.getStatus() == TripStatus.COMPLETED) {
                // Find the vehicle involved in this trip
                Vehicle vehicle = findVehicleById(vehicleList, trip.getVehicle().getId());
                if (vehicle != null) {
                    // Update the current port of the vehicle to the arrival port of the trip
                    vehicle.setCurrentPort(trip.getArrivalPort());
                }
            }
        }
    }

    private static Vehicle findVehicleById(List<Vehicle> vehicleList, String vehicleId) {
        for (Vehicle vehicle : vehicleList) {
            if (vehicle.getId().equals(vehicleId)) {
                return vehicle;
            }
        }
        return null;
    }



    // Method to read all trips from a file
    public static List<Trip> readFromFile(String filename, List<Vehicle> vehicleList, List<Ports> portsList) throws IOException {
        List<Trip> tripList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");

                // Here add code to find the vehicle and ports by ID and create a Trip object
                // For now, I'm just taking the first item from each list as a placeholder
                Vehicle vehicle = vehicleList.get(0);
                Ports departurePort = portsList.get(0);
                Ports arrivalPort = portsList.get(1);
                LocalDateTime departureDate = LocalDateTime.parse(details[3], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime arrivalDate = LocalDateTime.parse(details[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                TripStatus status = TripStatus.valueOf(details[5]);

                Trip trip = new Trip(vehicle, departurePort, arrivalPort, departureDate, arrivalDate, status);
                tripList.add(trip);
                updateVehiclePortsAfterTripCompletion(tripList);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripList;
    }


    // Method to save all trips to a file
    public static void saveAllToFile(String filename, List<Trip> tripList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Trip trip : tripList) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s%n", trip.getVehicle().getId(), trip.getDeparturePort().getId(), trip.getArrivalPort().getId(), trip.getDepartureDate().format(formatter), trip.getArrivalDate().format(formatter), trip.getStatus()));
            }
        }
    }

    public static void updateVehiclePortsAfterTripCompletion(List<Trip> tripList) {
        for (Trip trip : tripList) {
            if (trip.getStatus() == Trip.TripStatus.COMPLETED) {
                Vehicle vehicle = trip.getVehicle();
                vehicle.setCurrentPort(trip.getArrivalPort());
            }
        }
    }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Vehicle ID: %s, Departure Port ID: %s, Arrival Port ID: %s, Departure Date: %s, Arrival Date: %s, Status: %s",
                vehicle.getId(), departurePort.getId(), arrivalPort.getId(), departureDate.format(formatter), arrivalDate.format(formatter), status);
    }


    // Enum to represent the different statuses a trip can have
    public enum TripStatus {
        PLANNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}