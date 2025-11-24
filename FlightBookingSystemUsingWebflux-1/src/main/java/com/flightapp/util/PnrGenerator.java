package com.flightapp.util;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class PnrGenerator {
    public String generate() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
