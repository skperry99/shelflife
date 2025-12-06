package org.saper.shelflife.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void canSetAndGetBasicFields() {
        User user = new User();

        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPasswordHash("hashed-password");
        user.setDisplayName("Alice");

        assertEquals("alice", user.getUsername());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("hashed-password", user.getPasswordHash());
        assertEquals("Alice", user.getDisplayName());
    }

    @Test
    void onUpdateSetsUpdatedAtTimestamp() {
        User user = new User();

        // Initially, updatedAt may be null
        Instant before = user.getUpdatedAt();
        assertNull(before);

        // When onUpdate is called, updatedAt should be set
        user.onUpdate();

        Instant after = user.getUpdatedAt();
        assertNotNull(after);
    }
}
