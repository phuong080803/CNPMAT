package com.example.cdcnpmat.activities;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RateLimiter {
    private final ConcurrentHashMap<String, Long> requestMap = new ConcurrentHashMap<>();
    private final long timeWindowMillis;
    private final int maxRequests;

    public RateLimiter(long timeWindowMillis, int maxRequests) {
        this.timeWindowMillis = timeWindowMillis;
        this.maxRequests = maxRequests;
    }

    public synchronized boolean allowRequest(String key) {
        long currentTime = System.currentTimeMillis();
        requestMap.entrySet().removeIf(entry -> currentTime - entry.getValue() > timeWindowMillis);

        long count = requestMap.values().stream().filter(time -> currentTime - time <= timeWindowMillis).count();
        if (count >= maxRequests) {
            return false;
        }

        requestMap.put(key + "_" + currentTime, currentTime);
        return true;
    }
}
