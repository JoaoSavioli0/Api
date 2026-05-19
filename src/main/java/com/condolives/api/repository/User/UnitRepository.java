package com.condolives.api.repository.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.condolives.api.entity.User.Unit;

public interface UnitRepository extends JpaRepository<Unit, UUID> {

    List<Unit> findAllByCondominiumIdOrderByTypeAscIdentifierAsc(UUID condominiumId);

    Optional<Unit> findByIdAndCondominiumId(UUID id, UUID condominiumId);
}
