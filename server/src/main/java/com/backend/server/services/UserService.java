package com.backend.server.services;

import com.backend.server.DTO.UserDTO;
import com.backend.server.entities.Doctor;
import com.backend.server.entities.Patient;
import com.backend.server.entities.User;
import com.backend.server.repositories.DoctorRepository;
import com.backend.server.repositories.PatientRepository;
import com.backend.server.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private final PatientRepository patientRepository;

    @Autowired
    private final DoctorRepository doctorRepository;

    public User updateUser(String email, UserDTO.UserUpdateProfileDTO request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
        // If the user is a patient, update patient-specific fields
        if (user instanceof Patient patient) {
            if (request.getPhoneNumber() != null) {
                patient.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getHeight() != null) {
                patient.setHeight(request.getHeight());
            }
            if (request.getWeight() != null) {
                patient.setWeight(request.getWeight());
            }
            if (request.getBloodType() != null) {
                patient.setBloodType(request.getBloodType());
            }
            if (request.getConditions() != null) {
                patient.setConditions(request.getConditions());
            }
            System.out.println(patient);
            return patientRepository.save(patient); // 🔁 Save and return as Patient
        }
        else if(user instanceof Doctor doctor){
            if (request.getSpecialization() != null) {
                doctor.setSpecialization(request.getSpecialization());
            }
            if (request.getStartedPracticingAt() != null) {
                doctor.setStartedPracticingAt(request.getStartedPracticingAt());
            }
            if (request.getEducation() != null) {
                doctor.setEducation(request.getEducation());
            }
            if (request.getBio() != null) {
                doctor.setBio(request.getBio());
            }
            return doctorRepository.save(doctor); // 🔁 Save and return as Doctor
        }
    return userRepository.save(user);
    }

    @Transactional
    public Boolean deleteUser(String email) {
        // First, get the user by email
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) return false;

        User user = optionalUser.get();

        // If the user is a patient, delete from the patients table first
        if (user instanceof Patient patient) {
            patientRepository.deleteById(patient.getId());
        }

        else if(user instanceof Doctor doctor){
            doctorRepository.deleteById(doctor.getId());
        }

        // Now delete the user
        userRepository.deleteById(user.getId());

        return true;
    }



}
