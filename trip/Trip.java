package trip;

import ports.Ports;
import vehicle.Vehicle;

import java.io.*;
import java.time.LocalDate;
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

    public Trip() {
        this.vehicle = null;
        this.departurePort = null;
        this.arrivalPort = null;
        this.departureDate = null;
        this.arrivalDate = null;
        this.status = null;
    }

    public Trip(Vehicle vehicle, Ports departurePort, Ports arrivalPort, LocalDateTime departureDate, LocalDateTime arrivalDate, TripStatus status) {
        this.vehicle = vehicle;
        this.departurePort = departurePort;
        this.arrivalPort = arrivalPort;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.status = status;
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
        this.status = status;
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
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
            }
        }

        LocalDateTime arrivalDate = null;
        while (arrivalDate == null) {
            try {
                System.out.print("Enter arrival date (yyyy-MM-dd HH:mm): ");
                String arrivalDateString = reader.readLine();
                arrivalDate = LocalDateTime.parse(arrivalDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format.");
            }
        }

        TripStatus status = null;
        while (status == null) {
            try {
                System.out.print("Enter trip status (PLANNED/IN_PROGRESS/COMPLETED/CANCELLED): ");
                String statusString = reader.readLine();
                status = TripStatus.valueOf(statusString.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status.");
            }
        }

        Trip trip = new Trip(vehicle, departurePort, arrivalPort, departureDate, arrivalDate, status);
        tripList.add(trip);
        trip.saveToFile(filename, vehicleList, portsList);
    }


    public static void viewAllTrips(List<Trip> tripList) {
        for (Trip trip : tripList) {
            System.out.println(trip);
        }
    }

    public static void updateTrip(List<Trip> tripList, List<Vehicle> vehicleList, List<Ports> portsList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter trip ID to update: ");
        String tripId = reader.readLine();
        Trip tripToUpdate = findTripById(tripList, tripId);
        if (tripToUpdate == null) {
            System.out.println("Invalid trip ID.");
            return;
        }
        System.out.print("Enter new vehicle ID: ");
        String vehicleId = reader.readLine();
        Vehicle vehicle = findVehicleById(vehicleList, vehicleId);
        if (vehicle != null) {
            tripToUpdate.setVehicle(vehicle);
        }
        System.out.print("Enter new departure port ID: ");
        String departurePortId = reader.readLine();
        Ports newDeparturePort = findPortById(portsList, departurePortId);
        if (newDeparturePort != null) {
            tripToUpdate.setDeparturePort(newDeparturePort);
        }
        System.out.print("Enter new arrival port ID: ");
        String arrivalPortId = reader.readLine();
        Ports newArrivalPort = findPortById(portsList, arrivalPortId);
        if (newArrivalPort != null) {
            tripToUpdate.setArrivalPort(newArrivalPort);
        }
        System.out.print("Enter new departure date (yyyy-MM-dd HH:mm): ");
        String departureDateInput = reader.readLine();
        try {
            LocalDateTime newDepartureDate = LocalDateTime.parse(departureDateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            tripToUpdate.setDepartureDate(newDepartureDate);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
        }
        System.out.print("Enter new arrival date (yyyy-MM-dd HH:mm): ");
        String arrivalDateInput = reader.readLine();
        try {
            LocalDateTime newArrivalDate = LocalDateTime.parse(arrivalDateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            tripToUpdate.setArrivalDate(newArrivalDate);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
        }
        System.out.print("Enter new status (PLANNED/IN_PROGRESS/COMPLETED/CANCELLED): ");
        String statusInput = reader.readLine();
        try {
            TripStatus newStatus = TripStatus.valueOf(statusInput.toUpperCase());
            tripToUpdate.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status.");
        }
        saveAllToFile(filename, tripList);
    }

    public static void deleteTrip(List<Trip> tripList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter index of trip to delete: ");
        String indexStr = reader.readLine();
        try {
            int index = Integer.parseInt(indexStr);
            if (index < 0 || index >= tripList.size()) {
                System.out.println("Invalid index.");
                return;
            }
            tripList.remove(index);
            saveAllToFile(filename, tripList);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    public void saveToFile(String filename, List<Vehicle> vehicleList, List<Ports> portsList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            writer.write(String.format("%s,%s,%s,%s,%s,%s%n", vehicle.getId(), departurePort.getId(), arrivalPort.getId(), departureDate.format(formatter), arrivalDate.format(formatter), status));
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
                .filter(trip -> trip.getVehicle().getId().equals(tripId))
                .findFirst()
                .orElse(null);
    }

    public static List<Trip> readFromFile(String filename, List<Vehicle> vehicleList, List<Ports> portsList) throws IOException {
        List<Trip> tripList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                Vehicle vehicle = findVehicleById(vehicleList, details[0]);
                Ports departurePort = findPortById(portsList, details[1]);
                Ports arrivalPort = findPortById(portsList, details[2]);
                LocalDateTime departureDate = LocalDateTime.parse(details[3], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime arrivalDate = LocalDateTime.parse(details[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                TripStatus status = TripStatus.valueOf(details[5]);
                Trip trip = new Trip(vehicle, departurePort, arrivalPort, departureDate, arrivalDate, status);
                tripList.add(trip);
            }
        }
        return tripList;
    }

    // Method to list all trips on a given day
    public static void listTripsOnGivenDay(List<Trip> tripList) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = reader.readLine();
        LocalDate givenDate = LocalDate.parse(dateStr, formatter);
        for (Trip trip : tripList) {
            LocalDate departureDate = trip.getDepartureDate().toLocalDate();
            LocalDate arrivalDate = trip.getArrivalDate().toLocalDate();
            if (givenDate.equals(departureDate) || givenDate.equals(arrivalDate)) {
                System.out.println(trip);
            }
        }
    }

    // Method to list all trips from day A to day B
    public static void listTripsFromDayAToDayB(List<Trip> tripList) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.print("Please enter Day A: ");
        String startDateStr = reader.readLine();
        System.out.print("Please enter Day B: ");
        String endDateStr = reader.readLine();
        LocalDate start = LocalDate.parse(startDateStr, formatter);
        LocalDate end = LocalDate.parse(endDateStr, formatter);
        for (Trip trip : tripList) {
            LocalDate departureDate = trip.getDepartureDate().toLocalDate();
            LocalDate arrivalDate = trip.getArrivalDate().toLocalDate();
            if ((departureDate.isAfter(start) || departureDate.equals(start)) && (arrivalDate.isBefore(end) || arrivalDate.equals(end))) {
                System.out.println(trip);
            }
        }
    }


    public static void saveAllToFile(String filename, List<Trip> tripList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Trip trip : tripList) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s%n", trip.getVehicle().getId(), trip.getDeparturePort().getId(), trip.getArrivalPort().getId(), trip.getDepartureDate().format(formatter), trip.getArrivalDate().format(formatter), trip.getStatus()));
            }
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Vehicle ID: %s, Departure Port ID: %s, Arrival Port ID: %s, Departure Date: %s, Arrival Date: %s, Status: %s",
                vehicle.getId(), departurePort.getId(), arrivalPort.getId(), departureDate.format(formatter), arrivalDate.format(formatter), status);
    }

    public enum TripStatus {
        PLANNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
