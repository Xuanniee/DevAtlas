package com.xuannie.devatlas.user.domain.repository;

import com.xuannie.devatlas.user.common.command.CreateUserCommand;
import com.xuannie.devatlas.user.common.command.UpdateUserCommand;
import com.xuannie.devatlas.user.domain.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRepository {
    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    boolean existByEmail(String email);

    List<User> findAll();

    void update(Long userId, UpdateUserCommand command);

    void delete(Long userId);

    void create(User user);
}
