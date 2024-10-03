package edu.upc.fdp.entity;

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
}
