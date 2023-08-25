package container;

public class Container {
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

    private static int containerCounter = 0;
    private String id;
    private double weight;
    private ContainerType type;

    public Container() {
        this(0.0, null);
    }

    public Container(double weight, ContainerType type) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative.");
        }
        this.id = "c-" + (++ containerCounter);
        this.weight = weight;
        this.type = type;
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
}
