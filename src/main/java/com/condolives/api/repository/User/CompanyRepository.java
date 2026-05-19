package com.condolives.api.repository.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.User.Company;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> findAllByCondominiumId(UUID condominiumId);

    List<Company> findAllByCondominiumIdAndActive(UUID condominiumId, Boolean active);

    Optional<Company> findByIdAndCondominiumId(UUID id, UUID condominiumId);

    boolean existsByCondominiumIdAndNameIgnoreCase(UUID condominiumId, String name);
}
