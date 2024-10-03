package edu.upc.fdp.persistence;

import edu.upc.fdp.entity.Employee;
import edu.upc.fdp.entity.Reservation;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class DataBase {
    public static List<Reservation> reservations = new ArrayList<>();

    public static List<Employee> employees = new ArrayList<>();
}
