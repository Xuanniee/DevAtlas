package com.xuannie.devatlas.user.application;

import com.xuannie.devatlas.user.api.response.AuthResponse;
import com.xuannie.devatlas.user.common.exception.InvalidCredentialsException;
import com.xuannie.devatlas.user.common.mapper.UserMapper;
import com.xuannie.devatlas.user.common.command.CreateUserCommand;
import com.xuannie.devatlas.user.common.command.LoginUserCommand;
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
    // TODO add the JWTservice

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
        this.userRepository.create(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public AuthResponse login(LoginUserCommand command) {
        // Retrieve the Login User and compare password
        User user = this.userRepository.findByEmail(command.email())
                .orElseThrow(() -> new UserDoesNotExistException(command.email()));

        if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

//        String jwtToken = this.jwtService.issue
        return UserMapper.toResponse(targetUser);
    }
}
