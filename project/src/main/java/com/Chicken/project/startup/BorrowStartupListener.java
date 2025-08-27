package com.Chicken.project.startup;

import com.Chicken.project.utils.BorrowStatusUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BorrowStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private  final BorrowStatusUpdater updater;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        updater.updateBorrowStatus();
    }
}
