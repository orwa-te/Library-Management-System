package com.task.library.repository;

import com.task.library.entity.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {
    BorrowingRecord findByBookIdAndPatronIdAndReturnDateIsNull(Long bookId, Long patronId);
}
