package com.project.hospital.service;

import com.project.hospital.entity.Patient;
import com.project.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient getPatientByUserId(Long userId) {
        // Use Streams to find the Patient by associated User ID
        return patientRepository.findAll().stream()
                .filter(patient -> patient.getUser() != null && patient.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Patient profile not found for user ID: " + userId));
    }

    public Patient updatePatient(Long id, Patient updatedPatient) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + id));
        
        if (updatedPatient.getAge() != null)           patient.setAge(updatedPatient.getAge());
        if (updatedPatient.getGender() != null)        patient.setGender(updatedPatient.getGender());
        if (updatedPatient.getContactNumber() != null) patient.setContactNumber(updatedPatient.getContactNumber());
        if (updatedPatient.getAddress() != null)       patient.setAddress(updatedPatient.getAddress());
        if (updatedPatient.getMedicalHistory() != null) patient.setMedicalHistory(updatedPatient.getMedicalHistory());
        if (updatedPatient.getAadhaarNumber() != null) patient.setAadhaarNumber(updatedPatient.getAadhaarNumber());
        
        // Also update full name in user table if provided
        if (updatedPatient.getUser() != null && updatedPatient.getUser().getFullName() != null) {
            patient.getUser().setFullName(updatedPatient.getUser().getFullName());
        }

        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public List<Patient> searchPatients(String searchVal) {
        // Use Streams to search patients by name or contact number
        String query = searchVal.toLowerCase();
        return patientRepository.findAll().stream()
                .filter(patient -> 
                    (patient.getUser() != null && patient.getUser().getFullName().toLowerCase().contains(query)) ||
                    (patient.getContactNumber() != null && patient.getContactNumber().contains(query)) ||
                    (patient.getAddress() != null && patient.getAddress().toLowerCase().contains(query))
                )
                .collect(Collectors.toList());
    }
}
