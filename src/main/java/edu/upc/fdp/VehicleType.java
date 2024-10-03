package edu.upc.fdp;

public enum VehicleType {
    CAR,
    MOTORCYCLE,
    TRUCK;

    public static VehicleType fromString(String vehicleTypeStr) {
        try {
            return VehicleType.valueOf(vehicleTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error: Tipo de vehículo no válido. Los tipos válidos son CAR, MOTORCYCLE, TRUCK.");
        }
    }
}
