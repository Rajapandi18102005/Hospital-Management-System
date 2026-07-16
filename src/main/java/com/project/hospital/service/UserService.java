package com.project.hospital.service;

import com.project.hospital.entity.Doctor;
import com.project.hospital.entity.Patient;
import com.project.hospital.entity.User;
import com.project.hospital.repository.DoctorRepository;
import com.project.hospital.repository.PatientRepository;
import com.project.hospital.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        // BCrypt-encode the password before persisting
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the User
        User savedUser = userRepository.save(user);
        
        // Automatically create associated Patient or Doctor profiles based on role
        if ("PATIENT".equalsIgnoreCase(savedUser.getRole())) {
            Patient patient = new Patient();
            patient.setUser(savedUser);
            patient.setAge(0);
            patient.setGender("Not Specified");
            patient.setContactNumber("");
            patient.setAddress("");
            patient.setMedicalHistory("No previous history recorded.");
            patientRepository.save(patient);
        } else if ("DOCTOR".equalsIgnoreCase(savedUser.getRole())) {
            Doctor doctor = new Doctor();
            doctor.setUser(savedUser);
            doctor.setSpecialization("General Practitioner");
            doctor.setContactNumber("");
            doctor.setEmail(savedUser.getEmail());
            doctor.setAvailability("Mon-Fri 9:00 AM - 5:00 PM");
            doctorRepository.save(doctor);
        }
        
        return savedUser;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password!"));

        // Verify with BCrypt — never compare plain text
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password!");
        }
        return user;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(String role) {
        // Use Streams to filter users by their role
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
