package com.condolives.api.entity.Post.Ticket;

import java.util.ArrayList;
import java.util.List;

import com.condolives.api.converter.PostStatusConverter;
import com.condolives.api.entity.Post.Post;
import com.condolives.api.enums.PostStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ticket")
@DiscriminatorValue("ticket")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends Post {

    private String title;

    private String description;

    private String location;

    @Convert(converter = PostStatusConverter.class)
    private PostStatus status;

    @Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ticket_category", joinColumns = @JoinColumn(name = "ticket_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories = new ArrayList<>();

    @Default
    @OneToMany(mappedBy = "ticket", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketImage> images = new ArrayList<>();

    @Default
    @Column(name = "show_name", nullable = false)
    private boolean showName = true;
}
