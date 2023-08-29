// Base class for all vehicles
class Vehicle {
    protected String id;
    protected String name;
    protected double currentFuel;
    protected double carryingCapacity;
    protected double fuelCapacity;
    protected String currentPort;
    protected int totalContainers;

    public Vehicle(String id, String name, double currentFuel, double carryingCapacity, double fuelCapacity) {
        this.id = id;
        this.name = name;
        this.currentFuel = currentFuel;
        this.carryingCapacity = carryingCapacity;
        this.fuelCapacity = fuelCapacity;
        this.currentPort = null; // Sailaway
        this.totalContainers = 0;
    }

    public void move(String port) {
        if (currentPort == null) {
            System.out.println(name + " is sailing away.");
        } else {
            System.out.println(name + " is moving from " + currentPort + " to " + port);
        }
        currentPort = port;
    }

    public void loadContainer(String type) {
        if (currentPort != null) {
            if (canCarryContainer(type)) {
                System.out.println(name + " is loading a " + type + " container.");
                totalContainers++;
            } else {
                System.out.println(name + " cannot carry a " + type + " container.");
            }
        } else {
            System.out.println(name + " cannot load a container while sailing.");
        }
    }


    public void unloadContainer(String type) {
        if (currentPort != null) {
            if (totalContainers > 0) {
                System.out.println(name + " is unloading a " + type + " container.");
                totalContainers--;
            } else {
                System.out.println(name + " has no " + type + " container to unload.");
            }
        } else {
            System.out.println(name + " cannot unload a container while sailing.");
        }
    }
}

// Ship class, a type of vehicle
class Ship extends Vehicle {
    public Ship(String id, String name, double currentFuel, double carryingCapacity, double fuelCapacity) {
        super(id, name, currentFuel, carryingCapacity, fuelCapacity);
    }

    public boolean canCarryContainer(String type) {
        return true; // Ships can carry all types of containers
    }
}

// Truck class, a type of vehicle
class Truck extends Vehicle {
    private int trNumber;

    public Truck(int trNumber, String name, double currentFuel, double carryingCapacity, double fuelCapacity) {
        super("tr-" + trNumber, name, currentFuel, carryingCapacity, fuelCapacity);
        this.trNumber = trNumber;
    }

    public boolean canCarryContainer(String type) {
        // Implement logic to check if this truck can carry the given container type
        if (type.equals("dry storage") || type.equals("open top") || type.equals("open side")) {
            return true;
        }
        return false;
    }

}

// ReeferTruck class, a specialized type of truck
class ReeferTruck extends Truck {
    public ReeferTruck(int trNumber, String name, double currentFuel, double carryingCapacity, double fuelCapacity) {
        super(trNumber, name, currentFuel, carryingCapacity, fuelCapacity);
    }

    @Override
    public boolean canCarryContainer(String type) {
        // Implement logic to check if this reefer truck can carry the given container type
        return type.equals("refrigerated");
    }
}

// TankerTruck class, a specialized type of truck
class TankerTruck extends Truck {
    public TankerTruck(int trNumber, String name, double currentFuel, double carryingCapacity, double fuelCapacity) {
        super(trNumber, name, currentFuel, carryingCapacity, fuelCapacity);
    }

    @Override
    public boolean canCarryContainer(String type) {
        // Implement logic to check if this tanker truck can carry the given container type
        return type.equals("liquid");
    }
}

public class Main {
    public static void main(String[] args) {
        Ship ship = new Ship("sh-001", "Ocean Voyager", 1000.0, 5000.0, 2000.0);
        Truck truck = new Truck(101, "CargoMaster", 100.0, 50.0, 150.0);
        ReeferTruck reeferTruck = new ReeferTruck(201, "FreezerExpress", 80.0, 40.0, 120.0);
        TankerTruck tankerTruck = new TankerTruck(301, "LiquidHauler", 70.0, 35.0, 100.0);

        ship.move("Port A");
        ship.loadContainer("dry storage");

        truck.move("Port B");
        truck.loadContainer("dry storage");

        reeferTruck.move("Port C");
        reeferTruck.loadContainer("refrigerated");

        tankerTruck.move("Port D");
        tankerTruck.loadContainer("liquid");
    }
}
