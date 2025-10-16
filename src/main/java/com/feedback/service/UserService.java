package com.feedback.service;

import com.feedback.domain.User;
import com.feedback.dto.UserDTO;
import com.feedback.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public User save(UserDTO user) {
        User userToSave = null;
        if (user.getId() != null) {
            userToSave = findById(user.getId());
            if (userToSave == null) {
                return null;
            }
        }
        if (userToSave == null) {
            // Creation path: check for duplicate email before persisting
            if (user.getEmail() != null && userRepo.existsByEmail(user.getEmail())) {
                // Prefer a typed exception in real app (e.g., DuplicateEmailException)
                throw new IllegalStateException("A user with this email already exists: " + user.getEmail());
            }
            userToSave = new User();
        } else {
            // Update path: if you want to prevent changing to an existing email, validate here
            String newEmail = user.getEmail();
            if (newEmail != null && !newEmail.equals(userToSave.getEmail()) && userRepo.existsByEmail(newEmail)) {
                throw new IllegalStateException("Cannot update email. Another user already uses: " + newEmail);
            }
        }
        updateEntityFromDto(user, userToSave);
        return userRepo.save(userToSave);
    }

    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }

    public boolean existsById(Long id) {
        return userRepo.existsById(id);
    }

    private void updateEntityFromDto(UserDTO dto, User entity) {
        if (dto == null || entity == null) return;
        entity.setPassword("pwd1234");
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setFullName(dto.getFullName());
        entity.setBio(dto.getBio());
        entity.setPicture(dto.getPicture());
    }

    public static UserDTO getUserDTO(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());
        userDto.setBio(user.getBio());
        userDto.setPicture(user.getPicture());
        return userDto;
    }
}