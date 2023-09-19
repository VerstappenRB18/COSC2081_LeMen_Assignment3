//package ports;
//
//import trip.Trip;
//import vehicle.Vehicle;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.List;
//import java.util.Scanner;
//
//public class PortManagementSystem {
//    public static void main(String[] args) throws IOException {
//        Scanner scanner = new Scanner(System.in);
//        List<Ports> portsList = Ports.readFromFile(Ports.filename);
////        List<Vehicle> vehicleList = Vehicle.readFromFile(Vehicle.filename); // replace with your actual method to read vehicles from file
//        List<Trip> tripList = Trip.readFromFile(Trip.filename, vehicleList, portsList);
//
//        while (true) {
//            System.out.println("1. Create new port");
//            System.out.println("2. View all ports");
//            System.out.println("3. Update existing port");
//            System.out.println("4. Delete existing port");
//            System.out.println("5. Create new trip");
//            System.out.println("6. View all trips");
//            System.out.println("7. Update existing trip");
//            System.out.println("8. Delete existing trip");
//            System.out.println("9. Exit");
//            System.out.print("Please select an option: ");
//            int choice = scanner.nextInt();
//
//            switch (choice) {
//                case 1 -> Ports.createPort(portsList, Ports.filename);
//                case 2 -> {
//                    for (Ports port : portsList) {
//                        System.out.println(port);
//                    }
//                }
//                case 3 -> Ports.updatePort(portsList, Ports.filename);
//                case 4 -> Ports.deletePort(portsList, Ports.filename);
//                case 5 -> Trip.createTrip(tripList, vehicleList, portsList, Trip.filename);
//                case 6 -> Trip.viewAllTrips(tripList);
//                case 7 -> Trip.updateTrip(tripList, Trip.filename);
//                case 8 -> Trip.deleteTrip(tripList, Trip.filename);
//                case 9 -> {
//                    System.out.println("Exiting...");
//                    return;
//                }
//                default -> System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }
//}