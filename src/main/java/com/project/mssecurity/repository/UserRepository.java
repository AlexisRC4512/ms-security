package com.project.mssecurity.repository;

import com.project.mssecurity.entity.UserEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity>findByEmail(String email);
    @Query("SELECT ut.id FROM UserEntity u JOIN u.userTypeId ut WHERE u.id = :userId")
    List<Long   > findUserTypeIdsByUserId(@Param("userId") Long userId);
    Optional<UserEntity>findByNumDoc(String numDoc);
}
