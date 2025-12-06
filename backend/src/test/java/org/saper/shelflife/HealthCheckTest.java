package org.saper.shelflife;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HealthCheckTest {

    @Test
    void healthCheckPasses() {
        // Simple "always true" test just to verify CI and JUnit wiring
        assertTrue(true);
    }
}
