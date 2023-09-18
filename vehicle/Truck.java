    package vehicle;

    import container.Container;
    import ports.Ports;

    public class Truck extends Vehicle {
        public enum TruckType {
            BASIC,
            REEFER,
            TANKER
        }

        private final TruckType truckType;

        public Truck(String name, TruckType truckType, double carryingCapacity, double fuelCapacity, double currentFuel) {
            this.name = name;
            this.truckType = truckType;
            this.carryingCapacity = carryingCapacity;
            this.fuelCapacity = fuelCapacity;
            this.currentFuel = currentFuel;
            this.id = generateVehicleId();
        }

        @Override
        protected String generateVehicleId() {
            return "tr-" + (++vehicleCounter);
        }

        public boolean addContainer(Container container) {
            // Validate container type based on truck type
            switch (truckType) {
                case BASIC -> {
                    if (container.getType() == Container.ContainerType.REFRIGERATED
                            || container.getType() == Container.ContainerType.LIQUID) {
                        return false;
                    }
                }
                case REEFER -> {
                    if (container.getType() != Container.ContainerType.REFRIGERATED) {
                        return false;
                    }
                }
                case TANKER -> {
                    if (container.getType() != Container.ContainerType.LIQUID) {
                        return false;
                    }
                }
            }

            super.addContainer(container);
            return true;
        }

        @Override
        public boolean canMoveToPort(Ports currentPort, Ports targetPort) {
            if (!targetPort.isLandingAbility()) {
                return false;
            }
            return super.canMoveToPort(currentPort, targetPort);
        }

        public TruckType getTruckType() {
            return truckType;
        }

        @Override
        public String toCSVFormat() {
            return String.join(",",
                    this.getId(),
                    this.name,
                    this.truckType.name(),
                    String.valueOf(this.carryingCapacity),
                    String.valueOf(this.currentFuel),
                    String.valueOf(this.fuelCapacity),
                    String.valueOf(this.containers.size())
            );
        }

        @Override
        public String toString() {
            return super.toString() + '\n' +
                    "Truck Type: " + truckType;
        }
    }
