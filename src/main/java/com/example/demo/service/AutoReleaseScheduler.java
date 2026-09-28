package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AutoReleaseScheduler {

    private static final Logger log = LoggerFactory.getLogger(AutoReleaseScheduler.class);

    private final BookingService bookingService;

    public AutoReleaseScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Runs every 30 seconds to check for confirmed bookings whose 10-minute check-in grace period has elapsed.
     * Automatically frees up the room and marks status as NO_SHOW.
     */
    @Scheduled(fixedRate = 30000)
    public void scanAndReleaseNoShows() {
        int releasedCount = bookingService.releaseNoShowBookings();
        if (releasedCount > 0) {
            log.info("AutoReleaseScheduler: Released {} inactive bookings back to available inventory", releasedCount);
        }
    }
}
