package org.saper.shelflife.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "works",
        indexes = {
                @Index(name = "idx_works_user_id", columnList = "user_id"),
                @Index(name = "idx_works_status", columnList = "status"),
                @Index(name = "idx_works_type", columnList = "type")
        })
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id", nullable = false, updatable = false)
    private Long id; // maps to work_id

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_works_user"))
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkType type = WorkType.BOOK;

    @Column(length = 255)
    private String creator; // author/director/etc.

    @Column(length = 100)
    private String genre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkStatus status = WorkStatus.TO_EXPLORE;

    @Column(name = "total_units")
    private Integer totalUnits; // pages / episodes / chapters

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(name = "started_at")
    private LocalDate startedAt;

    @Column(name = "finished_at")
    private LocalDate finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;

        // Defensive defaults in case something tried to persist nulls
        if (type == null) {
            type = WorkType.BOOK;
        }
        if (status == null) {
            status = WorkStatus.TO_EXPLORE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
