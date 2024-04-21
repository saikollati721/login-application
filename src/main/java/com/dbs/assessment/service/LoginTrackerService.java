package com.dbs.assessment.service;

import com.dbs.assessment.enums.LoginStatus;
import com.dbs.assessment.model.LoginTracker;
import com.dbs.assessment.repository.LoginTrackerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "loginTrackerService")
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class LoginTrackerService {

    private final LoginTrackerRepository repository;

    public List<LoginTracker> findByUserNameAndStatus(String userName, LoginStatus status) throws UsernameNotFoundException {
        return repository.findByUserNameAndStatus(userName, status);
    }

    public LoginTracker save(LoginTracker loginTracker) {
        return repository.save(loginTracker);
    }
}
