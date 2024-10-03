package edu.upc.fdp;

import edu.upc.fdp.entity.Employee;
import edu.upc.fdp.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class ParkingManagement {
    private List<Reservation> reservations = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        ParkingManagement parkingManagement = new ParkingManagement();
        parkingManagement.init();
    }

    // Método para mostrar reservas activas (que están ocurriendo ahora mismo)
    public void showCurrentReservations() {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        List<Reservation> currentReservations = reservations.stream()
                .filter(reservation -> reservation.getDate().isEqual(today)
                        && reservation.getStartTime().isBefore(currentTime)
                        && reservation.getEndTime().isAfter(currentTime))
                .collect(Collectors.toList());

        if (currentReservations.isEmpty()) {
            System.out.println("No hay reservas activas en este momento.");
        } else {
            System.out.println("Reservas activas en este momento:");
            for (Reservation reservation : currentReservations) {
                System.out.println(reservation);
            }
        }
    }

    public void init() {
        System.out.println("¡Bienvenido al Sistema de Gestión de Estacionamiento!");

        boolean exit = false;
        while (!exit) {
            System.out.println("\nSeleccione una opción:");
            System.out.println("1. Registrar nueva reserva");
            System.out.println("2. Mostrar reservas futuras");
            System.out.println("3. Mostrar franjas horarias libres y ocupadas");
            System.out.println("4. Mostrar reservas activas (ahora mismo)");
            System.out.println("5. Salir");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    registerNewReservation();
                    break;
                case "2":
                    showFutureReservations();
                    break;
                case "3":
                    showAvailableAndOccupiedSlotsForUserInput();
                    break;
                case "4":
                    showCurrentReservations();
                    break;
                case "5":
                    exit = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }


    // Método para registrar una nueva reserva
    public void registerNewReservation() {
        try {
            System.out.print("Ingrese su DNI: ");
            String dni = scanner.nextLine().trim();

            System.out.print("Ingrese su nombre: ");
            String name = scanner.nextLine().trim();

            System.out.print("Ingrese el nombre su empresa: ");
            String companyName = scanner.nextLine().trim();

            System.out.println("Ingrese la matrícula del vehículo:");
            String licensePlate = scanner.nextLine().trim();

            System.out.println("Ingrese el tipo de vehículo (CAR, MOTORCYCLE, TRUCK):");
            VehicleType vehicleType = VehicleType.fromString(scanner.nextLine().trim());

            boolean error = true;

            LocalDate date = LocalDate.now();
            while(error) {
                System.out.println("Ingrese la fecha de su reserva(DD/MM/YYY): ");
                String dateString = scanner.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try {
                    date = LocalDate.parse(dateString, formatter);
                    error = false;
                } catch (DateTimeParseException e) {
                    System.out.println("La fecha ingresada no tiene el formato correcto(DD/MM/YYY)");
                    System.out.print("¿Dese volver a intentarlo(Y/n)?" );
                    String respuesta = scanner.nextLine();
                    if(respuesta.equalsIgnoreCase("N")) return;
                }
            }

            error = true;
            LocalTime startTime = LocalTime.now();
            while(error) {
                System.out.println("Ingrese la hora de inicio de su reserva(hh:mm): ");
                String startTimeString = scanner.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                try {
                    startTime = LocalTime.parse(startTimeString, formatter);
                    error = false;
                } catch (DateTimeParseException e) {
                    System.out.println("La hora ingresada no tiene el formato correcto(hh:mm)");
                    System.out.print("¿Dese volver a intentarlo(Y/n)?" );
                    String respuesta = scanner.nextLine();
                    if(respuesta.equalsIgnoreCase("N")) return;
                }
            }

            error = true;
            LocalTime endTime = LocalTime.now();
            while(error) {
                System.out.println("Ingrese la hora de finalización de su reserva(hh:mm): ");
                String endTimeString = scanner.nextLine();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                try {
                    endTime = LocalTime.parse(endTimeString, formatter);
                    error = false;
                } catch (DateTimeParseException e) {
                    System.out.println("La hora ingresada no tiene el formato correcto(hh:mm)");
                    System.out.print("¿Dese volver a intentarlo(Y/n)?" );
                    String respuesta = scanner.nextLine();
                    if(respuesta.equalsIgnoreCase("N")) return;
                }
            }

            if (startTime.isAfter(endTime)) {
                System.out.println("Error: La hora de inicio no puede ser posterior a la hora de fin.");
                return;
            }

            System.out.println("Ingrese el nivel del estacionamiento:");
            int level = Integer.parseInt(scanner.nextLine().trim());

            Employee newEmployee = Employee.builder()
                    .dni(dni)
                    .name(name)
                    .companyName(companyName)
                    .licensePlate(licensePlate)
                    .build();

            if(validateEmployee(newEmployee)) {
                employees.add(newEmployee);
            }

            Reservation newReservation = Reservation.builder()
                    .generatedCode(UUID.randomUUID().toString())
                    .licensePlate(licensePlate)
                    .vehicleType(vehicleType)
                    .date(date)
                    .startTime(startTime)
                    .endTime(endTime)
                    .level(level)
                    .build();

            if (validateReservation(newReservation)) {
                reservations.add(newReservation);
                System.out.println("Reserva añadida exitosamente: " + newReservation);
            } else {
                System.out.println("Error: La reserva tiene un conflicto de horario.");
            }
        } catch (Exception e) {
            System.out.println("Error al registrar la reserva: " + e.getMessage());
        }
    }

    // Método para validar la reserva y evitar conflictos de horarios
    public boolean validateReservation(Reservation newReservation) {
        // Verificar que la reserva no entre en conflicto con reservas existentes
        for (Reservation existingReservation : reservations) {
            if (existingReservation.getDate().isEqual(newReservation.getDate())
                    && existingReservation.getLevel() == newReservation.getLevel()
                    && existingReservation.getVehicleType() == newReservation.getVehicleType()) {

                // Verificar si hay un solapamiento entre las reservas
                if ((newReservation.getStartTime().isBefore(existingReservation.getEndTime())
                        && newReservation.getEndTime().isAfter(existingReservation.getStartTime()))) {
                    System.out.println("Error: Conflicto de horarios con la reserva: " + existingReservation);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateEmployee(Employee newEmployee) {
        for (Employee employee : employees) {
            if(employee.getDni().equals(newEmployee.getDni()) &&
                    employee.getLicensePlate().equals(newEmployee.getLicensePlate())) {
                return false;
            }
        }
        return true;
    }

    // Método para mostrar todas las reservas futuras
    public void showFutureReservations() {
        List<Reservation> futureReservations = reservations.stream()
                .filter(Reservation::isFutureReservation)
                .sorted((r1, r2) -> {
                    if (r1.getDate().isEqual(r2.getDate())) {
                        return r1.getStartTime().compareTo(r2.getStartTime());
                    } else {
                        return r1.getDate().compareTo(r2.getDate());
                    }
                })
                .collect(Collectors.toList());

        if (futureReservations.isEmpty()) {
            System.out.println("No hay reservas futuras.");
        } else {
            System.out.println("Reservas futuras:");
            for (Reservation reservation : futureReservations) {
                System.out.println(reservation);
            }
        }
    }

    // Método para mostrar franjas horarias libres y ocupadas para un tipo de vehículo y fecha ingresados por el usuario
    public void showAvailableAndOccupiedSlotsForUserInput() {
        try {
            System.out.println("Ingrese la fecha (formato: yyyy-MM-dd):");
            LocalDate date = parseDate(scanner.nextLine().trim());

            System.out.println("Ingrese el tipo de vehículo (CAR, MOTORCYCLE, TRUCK):");
            VehicleType vehicleType = VehicleType.fromString(scanner.nextLine().trim());

            showAvailableAndOccupiedSlots(date, vehicleType);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Mostrar franjas horarias libres y ocupadas por tipo de vehículo y fecha
    public void showAvailableAndOccupiedSlots(LocalDate date, VehicleType vehicleType) {
        List<Reservation> reservationsForDate = reservations.stream()
                .filter(r -> r.getDate().isEqual(date) && r.getVehicleType() == vehicleType)
                .sorted((r1, r2) -> r1.getStartTime().compareTo(r2.getStartTime()))
                .collect(Collectors.toList());

        if (reservationsForDate.isEmpty()) {
            System.out.println("No hay reservas para " + vehicleType + " el " + date);
            return;
        }

        System.out.println("Horarios ocupados para " + vehicleType + " el " + date + ":");
        LocalTime openingTime = LocalTime.of(8, 0);  // Suponiendo que el estacionamiento abre a las 8:00
        LocalTime closingTime = LocalTime.of(18, 0); // Suponiendo que el estacionamiento cierra a las 18:00

        LocalTime currentTime = openingTime;
        for (Reservation reservation : reservationsForDate) {
            if (currentTime.isBefore(reservation.getStartTime())) {
                System.out.println("Libre de " + currentTime + " a " + reservation.getStartTime());
            }
            System.out.println("Ocupado de " + reservation.getStartTime() + " a " + reservation.getEndTime());
            currentTime = reservation.getEndTime();
        }

        if (currentTime.isBefore(closingTime)) {
            System.out.println("Libre de " + currentTime + " a " + closingTime);
        }
    }

    // Método para validar y parsear la fecha ingresada
    public LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error: Formato de fecha no válido. Use el formato yyyy-MM-dd.");
        }
    }

    // Método para validar y parsear la hora ingresada
    public LocalTime parseTime(String timeStr) {
        try {
            return LocalTime.parse(timeStr, timeFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Error: Formato de hora no válido. Use el formato HH:mm.");
        }
    }
}