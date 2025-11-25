package org.saper.shelflife.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = {"username"}),
                @UniqueConstraint(name = "uk_users_email", columnNames = {"email"})
        }
)
public class User {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // maps to user_id

    @Setter
    @Getter
    @Column(nullable = false, length = 50)
    private String username;

    @Setter
    @Getter
    @Column(nullable = false, length = 255)
    private String email;

    @Setter
    @Getter
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Setter
    @Getter
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Getter
    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Work> works = new ArrayList<>();

    // Getters and Setters

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

}
