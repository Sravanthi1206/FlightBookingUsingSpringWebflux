package com.flightapp.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PnrGeneratorTest {

    private final PnrGenerator generator = new PnrGenerator();

    @Test
    void generate_returns8UppercaseCharacters() {
        String pnr = generator.generate();

        assertNotNull(pnr);
        assertEquals(8, pnr.length());
        assertEquals(pnr.toUpperCase(), pnr);
        assertTrue(pnr.matches("[A-Z0-9]+"));
    }

    @Test
    void generate_twoCalls_returnDifferentPNRs() {
        String p1 = generator.generate();
        String p2 = generator.generate();

        assertNotEquals(p1, p2);
    }
}
