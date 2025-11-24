package com.flightapp.util;

import org.junit.jupiter.api.Test;

class PnrGeneratorTest {

    private final PnrGenerator generator = new PnrGenerator();

    @Test
    void generate_returns8UppercaseCharacters() {
        String pnr = generator.generate();

        assert pnr != null;
        assert pnr.length() == 8;
        assert pnr.equals(pnr.toUpperCase());
        assert pnr.matches("[A-Z0-9]+");
    }

    @Test
    void generate_twoCalls_returnDifferentPNRs() {
        String p1 = generator.generate();
        String p2 = generator.generate();

        assert !p1.equals(p2);
    }
}
