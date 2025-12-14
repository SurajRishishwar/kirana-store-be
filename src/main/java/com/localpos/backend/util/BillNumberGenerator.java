package com.localpos.backend.util;

import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class BillNumberGenerator {

    private final AtomicLong counter = new AtomicLong(1);
    private int currentYear = Year.now().getValue();

    public synchronized String generateBillNumber() {
        int year = Year.now().getValue();

        // Reset counter if year has changed
        if (year != currentYear) {
            currentYear = year;
            counter.set(1);
        }

        long sequence = counter.getAndIncrement();
        String sequenceStr = String.format("%06d", sequence);

        return String.format("BILL-%d-%s", year, sequenceStr);
    }
}
