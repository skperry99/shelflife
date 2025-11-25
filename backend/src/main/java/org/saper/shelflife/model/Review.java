package org.saper.shelflife.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reviews_user_work",
                        columnNames = {"user_id", "work_id"}
                )
        },
        indexes = {
                @Index(name = "idx_reviews_user_id", columnList = "user_id"),
                @Index(name = "idx_reviews_work_id", columnList = "work_id")
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id", nullable = false, updatable = false)
    private Long id; // review_id

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reviews_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reviews_work"))
    private Work work;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(length = 255)
    private String title;

    @Lob
    private String body;

    @Column(name = "is_private", nullable = false)
    private boolean privateReview = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // getters/setters
}
