package com.condolives.api.repository.Condominium;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.condolives.api.entity.Condominium;

@Repository
public interface CondominiumRepository extends JpaRepository<Condominium, UUID> {
    Optional<Condominium> findByCode(String code);
}
