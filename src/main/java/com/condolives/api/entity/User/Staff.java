package com.condolives.api.entity.User;

import java.time.LocalDate;
import java.util.UUID;

import com.condolives.api.converter.StaffCategoryConverter;
import com.condolives.api.enums.StaffCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", updatable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserAccount user;

    @Column(name = "condominium_id", nullable = false, updatable = false)
    private UUID condominiumId;

    private String name;

    private String phone;

    @Column(length = 11)
    private String cpf;

    private String rg;

    @Column(name = "job_title")
    private String jobTitle;

    @Convert(converter = StaffCategoryConverter.class)
    @Column(nullable = false)
    private StaffCategory category;

    private String address;

    @Column(name = "company_id")
    private UUID companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private Company company;

    @Column(name = "joined_at", nullable = false)
    private LocalDate joinedAt;

    @Setter
    @Column(nullable = false)
    private Boolean active;
}
