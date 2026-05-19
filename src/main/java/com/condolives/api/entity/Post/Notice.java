package com.condolives.api.entity.Post;

import com.condolives.api.converter.ImportanceConverter;
import com.condolives.api.enums.Importance;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notice")
@DiscriminatorValue("notice")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends Post {

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Convert(converter = ImportanceConverter.class)
    @Column(nullable = false, length = 10)
    private Importance importance;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "target_value", length = 100)
    private String targetValue;
}
