package com.xuannie.devatlas.user.domain.repository;

import com.xuannie.devatlas.user.domain.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserRepository {
    Optional<User> findById(@Param("userId") Long userId);

    Optional<User> findByEmail(@Param("email") String email);

    boolean existByEmail(@Param("email") String email);

    List<User> findAll();

    void update(
            @Param("userId") Long userId,
            @Param("user") User updatedUser
    );

    void delete(@Param("userId") Long userId);

    void insert(@Param("user") User updatedUser);
}
