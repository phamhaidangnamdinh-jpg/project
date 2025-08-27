package com.Chicken.project.utils;

import com.Chicken.project.entity.Borrow.Borrow;
import com.Chicken.project.entity.Borrow.BorrowStatus;
import com.Chicken.project.repository.BorrowRepo;
import com.Chicken.project.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BorrowStatusUpdater {
    private final BorrowRepo repo;
    private static final Logger log =  LoggerFactory.getLogger(BorrowStatusUpdater.class);
    public void updateBorrowStatus(){
        log.info("Updating borrow status");
        List<Borrow> borrows = repo.findByStatusNot(BorrowStatus.RETURNED);
        LocalDate today = LocalDate.now();
        for(Borrow borrow : borrows){
            if(borrow.getReturnDate()==null)continue;
            if(borrow.getReturnDate().isBefore(today)) {
                borrow.setStatus(BorrowStatus.LATE);
            }
            else borrow.setStatus(BorrowStatus.NOT_RETURNED);
        }
        repo.saveAll(borrows);
    }
}
