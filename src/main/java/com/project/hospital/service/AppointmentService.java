package com.project.hospital.service;

import com.project.hospital.entity.Appointment;
import com.project.hospital.entity.Doctor;
import com.project.hospital.entity.Patient;
import com.project.hospital.repository.AppointmentRepository;
import com.project.hospital.repository.DoctorRepository;
import com.project.hospital.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Appointment bookAppointment(Appointment appointment) {
        // Fetch full patient and doctor details to ensure they exist and link correctly
        Patient patient = patientRepository.findById(appointment.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + appointment.getPatient().getId()));
        
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + appointment.getDoctor().getId()));
        
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setStatus("PENDING");
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment updatedAppointment) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
        
        if (updatedAppointment.getStatus() != null) {
            appointment.setStatus(updatedAppointment.getStatus().toUpperCase());
        }
        if (updatedAppointment.getReason() != null) {
            appointment.setReason(updatedAppointment.getReason());
        }
        if (updatedAppointment.getPrescription() != null) {
            appointment.setPrescription(updatedAppointment.getPrescription());
        }
        if (updatedAppointment.getAppointmentDate() != null) {
            appointment.setAppointmentDate(updatedAppointment.getAppointmentDate());
        }
        
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    // Java Stream manipulations as requested by user
    public List<Appointment> getAppointmentsByPatientUserId(Long userId) {
        return appointmentRepository.findAll().stream()
                .filter(appt -> appt.getPatient() != null && 
                                appt.getPatient().getUser() != null && 
                                appt.getPatient().getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByDoctorUserId(Long userId) {
        return appointmentRepository.findAll().stream()
                .filter(appt -> appt.getDoctor() != null && 
                                appt.getDoctor().getUser() != null && 
                                appt.getDoctor().getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findAll().stream()
                .filter(appt -> appt.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public long countAppointmentsByStatus(String status) {
        return appointmentRepository.findAll().stream()
                .filter(appt -> appt.getStatus().equalsIgnoreCase(status))
                .count();
    }
}
