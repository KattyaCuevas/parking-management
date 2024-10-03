package edu.upc.fdp.entity;

import edu.upc.fdp.VehicleType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class Reservation {
    private String generatedCode;
    
    private String licensePlate;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDate date;

    private int level;
    private VehicleType vehicleType;

    // Método para verificar si es una reserva futura
    public boolean isFutureReservation() {
        return date.isAfter(LocalDate.now()) || (date.isEqual(LocalDate.now()) && startTime.isAfter(LocalTime.now()));
    }

    @Override
    public String toString() {
        return "Reserva: " + generatedCode + " | Tipo de vehículo: " + vehicleType + " | Matrícula: " + licensePlate + " | Fecha: " + date + " | Hora de inicio: " + startTime + " | Hora de fin: " + endTime + " | Nivel: " + level;
    }
}
