package com.xuannie.devatlas.user.application;

import com.xuannie.devatlas.common.utils.PatchUtils;
import com.xuannie.devatlas.user.api.request.LoginUserRequest;
import com.xuannie.devatlas.user.api.response.AuthResponse;
import com.xuannie.devatlas.user.common.command.*;
import com.xuannie.devatlas.user.common.exception.InvalidCredentialsException;
import com.xuannie.devatlas.user.common.mapper.UserMapper;
import com.xuannie.devatlas.user.common.exception.EmailAlreadyExistsException;
import com.xuannie.devatlas.user.common.exception.UserDoesNotExistException;
import com.xuannie.devatlas.user.domain.model.User;
import com.xuannie.devatlas.user.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtServiceImpl jwtService;

    @Override
    public AuthResponse register(CreateUserCommand command) {
        // Verify if the Email has been registered before
        if (this.userRepository.existByEmail(command.email())) {
            // Log and throw exception
            throw new EmailAlreadyExistsException(command.email());
        }

        // TODO Verify if the Password fulfils Password Policy e.g. special char
        // Hash password and store
        String passwordHash = passwordEncoder.encode(command.password());
        User user = User.builder()
                .name(command.name())
                .email(command.email())
                .passwordHash(passwordHash)
                .build();
        this.userRepository.insert(user);

        // Login user after creating account
        LoginUserCommand loginCommand = UserCommandBuilder.from(
                new LoginUserRequest(command.email(), command.password())
        );
        return this.login(loginCommand);
    }

    @Override
    public AuthResponse login(LoginUserCommand command) {
        // Retrieve the Login User and validate password
        User user = this.userRepository.findByEmail(command.email())
                .orElseThrow(() -> new UserDoesNotExistException(command.email()));
        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String jwtToken = this.jwtService.issueToken(user.getId());
        return UserMapper.toResponse(jwtToken);
    }

    @Override
    public AuthResponse update(Long userId, UpdateUserCommand command) {
        // Retrieve the Target User
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException(userId));

        // Update the fields user wants to update
        PatchUtils.ifPresent(command.newName(), user::setName);
        PatchUtils.ifPresent(command.newEmail(), user::setEmail);
        PatchUtils.ifPresent(command.newPassword(), newPassword -> {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        });

        // Return updated user details
        this.userRepository.update(userId, user);
        return UserMapper.toResponse(user);
    }

    @Override
    public AuthResponse delete(DeleteUserCommand command) {
        // Delete the User if present
        User user = this.userRepository.findById(command.userId())
                .orElseThrow(() -> new UserDoesNotExistException(command.userId()));

        this.userRepository.delete(user.getId());
        return UserMapper.toResponse(user);
    }
}
