package com.condolives.api.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.condolives.api.converter.MemberRoleConverter;
import com.condolives.api.enums.MemberRole;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "condo_member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CondoMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Setter
    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserAccount user;

    @Column(name = "condominium_id", nullable = false, updatable = false)
    private UUID condominiumId;

    @Builder.Default
    @Convert(converter = MemberRoleConverter.class)
    @Column(nullable = false)
    private MemberRole role = MemberRole.RESIDENT;

    @Setter
    @Column(name = "unit_id")
    private UUID unitId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    private Unit unit;

    public String getUnitAddress() {
        return unit != null ? unit.getDisplayAddress() : null;
    }

    @Column(name = "joined_at", nullable = false)
    private LocalDate joinedAt;

    @Setter
    @Column(nullable = false)
    private Boolean active;

    @Column(name = "guardian_id")
    private UUID guardianId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guardian_id", insertable = false, updatable = false)
    private CondoMember guardian;

    @Builder.Default
    @OneToMany(mappedBy = "guardian", fetch = FetchType.LAZY)
    private List<CondoMember> dependents = new ArrayList<>();
}
