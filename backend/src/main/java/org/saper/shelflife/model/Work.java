package org.saper.shelflife.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = {"user", "sessions", "reviews"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "works",
        indexes = {
                @Index(name = "idx_works_user_id", columnList = "user_id"),
                @Index(name = "idx_works_status", columnList = "status"),
                @Index(name = "idx_works_type", columnList = "type")
        }
)
public class Work {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_id", nullable = false, updatable = false)
    private Long id; // maps to work_id

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_works_user")
    )
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
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // --- static factory (optional helper) ---

    public static Work createForUser(
            User user,
            String title,
            WorkType type,
            WorkStatus status
    ) {
        Work work = new Work();
        work.setUser(user);
        work.setTitle(title);
        work.setType(type != null ? type : WorkType.BOOK);
        work.setStatus(status != null ? status : WorkStatus.TO_EXPLORE);
        return work;
    }

    // --- relationship helpers ---

    public void addSession(Session session) {
        if (session == null) return;
        sessions.add(session);
        session.setWork(this);
    }

    public void removeSession(Session session) {
        if (session == null) return;
        sessions.remove(session);
        session.setWork(null);
    }

    public void addReview(Review review) {
        if (review == null) return;
        reviews.add(review);
        review.setWork(this);
    }

    public void removeReview(Review review) {
        if (review == null) return;
        reviews.remove(review);
        review.setWork(null);
    }

    // --- lifecycle hooks ---

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;

        // Defensive defaults in case something tried to persist nulls
        if (this.type == null) {
            this.type = WorkType.BOOK;
        }
        if (this.status == null) {
            this.status = WorkStatus.TO_EXPLORE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
