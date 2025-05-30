package com.backend.server.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Patient who booked the appointment
    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Doctor for the appointment
    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;


    // Stores the date of the appointment (e.g. 2025-05-01)
    private LocalDate date;

    // Stores the time (e.g. 14:00 for 2 PM)
    private LocalTime time;


    @Column(length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED
    }
}
