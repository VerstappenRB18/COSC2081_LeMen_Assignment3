package trip;

import ports.Ports;
import vehicle.Vehicle;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip {
    private Vehicle vehicle;
    private Ports departurePort;
    private Ports arrivalPort;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private TripStatus status;

    public static final String filename = "trips.txt";

    // Default constructor
    public Trip() {
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

        // Here you would add code to select a vehicle, departurePort, and arrivalPort from the lists
        // For now, I'm just taking the first item from each list as a placeholder
        Vehicle vehicle = vehicleList.get(0);
        Ports departurePort = portsList.get(0);
        Ports arrivalPort = portsList.get(1);

        System.out.print("Enter departure date (yyyy-MM-dd HH:mm): ");
        String departureDateString = reader.readLine();
        LocalDateTime departureDate = LocalDateTime.parse(departureDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        System.out.print("Enter arrival date (yyyy-MM-dd HH:mm): ");
        String arrivalDateString = reader.readLine();
        LocalDateTime arrivalDate = LocalDateTime.parse(arrivalDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        System.out.print("Enter status (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED): ");
        String statusString = reader.readLine();
        TripStatus status = TripStatus.valueOf(statusString.toUpperCase());

        Trip trip = new Trip(vehicle, departurePort, arrivalPort, departureDate, arrivalDate, status);
        tripList.add(trip);
        trip.saveToFile(filename);

        System.out.println("Trip created successfully.");
    }


    // Method to view all trips
    public static void viewAllTrips(List<Trip> tripList) {
        for (Trip trip : tripList) {
            System.out.println(trip);
        }
    }

    // Method to update an existing trip
    public static void updateTrip(List<Trip> tripList, String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("Enter trip index to update: ");
        int index = Integer.parseInt(reader.readLine());

        // Here add code to update the details of the trip at the specified index

        saveAllToFile(filename, tripList);
        System.out.println("Trip updated successfully.");
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
    public void saveToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            writer.write(String.format("%s,%s,%s,%s,%s,%s%n", vehicle.getId(), departurePort.getId(), arrivalPort.getId(), sdf.format(departureDate), sdf.format(arrivalDate), status));
        }
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tripList;
    }


    // Method to save all trips to a file
    public static void saveAllToFile(String filename, List<Trip> tripList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            for (Trip trip : tripList) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s%n", trip.getVehicle().getId(), trip.getDeparturePort().getId(), trip.getArrivalPort().getId(), sdf.format(trip.getDepartureDate()), sdf.format(trip.getArrivalDate()), trip.getStatus()));
            }
        }
    }

    // toString method to display trip details
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return String.format("Vehicle ID: %s, Departure Port ID: %s, Arrival Port ID: %s, Departure Date: %s, Arrival Date: %s, Status: %s",
                vehicle.getId(), departurePort.getId(), arrivalPort.getId(), sdf.format(departureDate), sdf.format(arrivalDate), status);
    }


    // Enum to represent the different statuses a trip can have
    public enum TripStatus {
        PLANNED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
