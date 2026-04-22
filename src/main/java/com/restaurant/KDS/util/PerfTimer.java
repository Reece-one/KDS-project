package com.restaurant.KDS.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public final class PerfTimer {

    private static final Logger log = LoggerFactory.getLogger(PerfTimer.class);
    private static final long SLA_MS = 1000;

    private PerfTimer() {}

    public static void time(String label, Runnable action) {
        long start = System.nanoTime();
        action.run();
        report(label, start);
    }

    public static <T> T time(String label, Supplier<T> action) {
        long start = System.nanoTime();
        T result = action.get();
        report(label, start);
        return result;
    }

    private static void report(String label, long start) {
        long ms = (System.nanoTime() - start) / 1_000_000;
        if (ms > SLA_MS) {
            log.warn("PERF {} took {}ms (SLA {}ms breached)", label, ms, SLA_MS);
        } else {
            log.info("PERF {} took {}ms", label, ms);
        }
    }
}
