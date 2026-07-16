package com.project.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private Integer age;
    
    private String gender;

    private String contactNumber;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    @Column(name = "aadhaar_number", nullable = true)
    private String aadhaarNumber;
}
