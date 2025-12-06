package org.saper.shelflife.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString(exclude = {"passwordHash", "works", "sessions", "reviews"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = {"username"}),
                @UniqueConstraint(name = "uk_users_email", columnNames = {"email"})
        }
)
public class User {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;   // maps to user_id

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Work> works = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // --- static factory ---

    public static User create(String username, String email, String encodedPassword, String displayName) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encodedPassword);
        u.setDisplayName(displayName);
        return u;
    }

    // --- relationship helpers ---

    public void addWork(Work work) {
        if (work == null) return;
        works.add(work);
        work.setUser(this);
    }

    public void removeWork(Work work) {
        if (work == null) return;
        works.remove(work);
        work.setUser(null);
    }

    public void addSession(Session session) {
        if (session == null) return;
        sessions.add(session);
        session.setUser(this);
    }

    public void removeSession(Session session) {
        if (session == null) return;
        sessions.remove(session);
        session.setUser(null);
    }

    public void addReview(Review review) {
        if (review == null) return;
        reviews.add(review);
        review.setUser(this);
    }

    public void removeReview(Review review) {
        if (review == null) return;
        reviews.remove(review);
        review.setUser(null);
    }

    // --- lifecycle hooks ---

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
