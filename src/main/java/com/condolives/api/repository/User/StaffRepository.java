package com.condolives.api.repository.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.User.Staff;
import com.condolives.api.enums.StaffCategory;

public interface StaffRepository extends JpaRepository<Staff, UUID> {

    @EntityGraph(attributePaths = { "user", "company" })
    List<Staff> findAllByCondominiumId(UUID condominiumId);

    @EntityGraph(attributePaths = { "user", "company" })
    List<Staff> findAllByCondominiumIdAndCategory(UUID condominiumId, StaffCategory category);

    @EntityGraph(attributePaths = { "user", "company" })
    Optional<Staff> findByIdAndCondominiumId(UUID id, UUID condominiumId);
}
