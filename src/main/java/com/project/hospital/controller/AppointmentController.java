package com.project.hospital.controller;

import com.project.hospital.entity.Appointment;
import com.project.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(appt -> new ResponseEntity<Object>(appt, HttpStatus.OK))
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Appointment not found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                });
    }

    @PostMapping
    public ResponseEntity<?> bookAppointment(@RequestBody Appointment appointment) {
        try {
            Appointment savedAppt = appointmentService.bookAppointment(appointment);
            return new ResponseEntity<>(savedAppt, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        try {
            Appointment updatedAppt = appointmentService.updateAppointment(id, appointmentDetails);
            return new ResponseEntity<>(updatedAppt, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Appointment cancelled/deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/patient/user/{userId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatientUserId(@PathVariable Long userId) {
        List<Appointment> appts = appointmentService.getAppointmentsByPatientUserId(userId);
        return new ResponseEntity<>(appts, HttpStatus.OK);
    }

    @GetMapping("/doctor/user/{userId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorUserId(@PathVariable Long userId) {
        List<Appointment> appts = appointmentService.getAppointmentsByDoctorUserId(userId);
        return new ResponseEntity<>(appts, HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(@PathVariable String status) {
        List<Appointment> appts = appointmentService.getAppointmentsByStatus(status);
        return new ResponseEntity<>(appts, HttpStatus.OK);
    }
}
