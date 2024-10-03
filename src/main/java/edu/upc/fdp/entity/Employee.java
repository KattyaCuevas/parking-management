package edu.upc.fdp.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Employee {
    private String dni;

    private String name;

    private String companyName;

    private String licensePlate;
}
