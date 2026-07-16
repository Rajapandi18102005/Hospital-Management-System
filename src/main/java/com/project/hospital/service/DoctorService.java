package com.project.hospital.service;

import com.project.hospital.entity.Doctor;
import com.project.hospital.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor getDoctorByUserId(Long userId) {
        // Use Streams to find Doctor profile by associated User ID
        return doctorRepository.findAll().stream()
                .filter(doctor -> doctor.getUser() != null && doctor.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for user ID: " + userId));
    }

    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));

        doctor.setSpecialization(updatedDoctor.getSpecialization());
        doctor.setContactNumber(updatedDoctor.getContactNumber());
        doctor.setEmail(updatedDoctor.getEmail());
        doctor.setAvailability(updatedDoctor.getAvailability());

        // Also update full name in user table if provided
        if (updatedDoctor.getUser() != null && updatedDoctor.getUser().getFullName() != null) {
            doctor.getUser().setFullName(updatedDoctor.getUser().getFullName());
        }

        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    public List<Doctor> searchDoctors(String searchVal) {
        // Use Streams to search doctors by name or specialization
        String query = searchVal.toLowerCase();
        return doctorRepository.findAll().stream()
                .filter(doctor -> 
                    (doctor.getUser() != null && doctor.getUser().getFullName().toLowerCase().contains(query)) ||
                    (doctor.getSpecialization() != null && doctor.getSpecialization().toLowerCase().contains(query))
                )
                .collect(Collectors.toList());
    }
}
