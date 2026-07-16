package com.project.hospital.controller;

import com.project.hospital.entity.Doctor;
import com.project.hospital.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id)
                .map(doctor -> new ResponseEntity<Object>(doctor, HttpStatus.OK))
                .orElseGet(() -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Doctor profile not found");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                });
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getDoctorByUserId(@PathVariable Long userId) {
        try {
            Doctor doctor = doctorService.getDoctorByUserId(userId);
            return new ResponseEntity<>(doctor, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        try {
            Doctor updatedDoctor = doctorService.updateDoctor(id, doctorDetails);
            return new ResponseEntity<>(updatedDoctor, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        try {
            doctorService.deleteDoctor(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Doctor profile deleted successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(@RequestParam String query) {
        List<Doctor> doctors = doctorService.searchDoctors(query);
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }
}
