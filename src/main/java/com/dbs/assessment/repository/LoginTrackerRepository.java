package com.dbs.assessment.repository;

import com.dbs.assessment.enums.LoginStatus;
import com.dbs.assessment.model.LoginTracker;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Transactional
@Repository
public interface LoginTrackerRepository extends BaseRepository<LoginTracker, Long> {
    List<LoginTracker> findByCreatedDateIsGreaterThanEqualAndCreatedDateIsLessThanEqualAndUserNameAndStatus(
            Timestamp startTime, Timestamp endTime, String userName, LoginStatus status);
}
