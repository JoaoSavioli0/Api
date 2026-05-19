package com.condolives.api.entity.User;

import java.util.UUID;

import com.condolives.api.converter.UnitTypeConverter;
import com.condolives.api.enums.UnitType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "unit")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "condominium_id", nullable = false, updatable = false)
    private UUID condominiumId;

    @Column(nullable = false)
    private String identifier;

    @Column
    private String block;

    @Column
    private String street;

    @Column
    private Integer floor;

    @Convert(converter = UnitTypeConverter.class)
    @Column(nullable = false)
    private UnitType type;

    public String getDisplayAddress() {
        return switch (type) {
            case APARTMENT -> {
                String base = "Apto " + identifier;
                yield block != null && !block.isBlank() ? base + " - Bloco " + block : base;
            }
            case HOUSE -> {
                String base = "Casa " + identifier;
                yield street != null && !street.isBlank() ? base + " - " + street : base;
            }
        };
    }
}
