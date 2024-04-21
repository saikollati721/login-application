package com.dbs.assessment.repository;

import com.dbs.assessment.model.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Transactional
@Repository
public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByUserName(String userName);
}
