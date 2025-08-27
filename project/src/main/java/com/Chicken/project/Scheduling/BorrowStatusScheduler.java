package com.Chicken.project.Scheduling;

import com.Chicken.project.utils.BorrowStatusUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BorrowStatusScheduler {
    private final BorrowStatusUpdater statusUpdater;

    @Scheduled(cron = "0 0 0 * * *")
    public void updateBorrowStatusNightly() {
        statusUpdater.updateBorrowStatus();
    }
}
