package com.dbs.assessment.service;

import com.dbs.assessment.dto.UserDTO;
import com.dbs.assessment.exception.UserNotFoundException;
import com.dbs.assessment.mapper.UserMapper;
import com.dbs.assessment.model.User;
import com.dbs.assessment.repository.UserRepository;
import com.dbs.assessment.request.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service(value = "userService")
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    public User loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = repository.findByUserName(userName);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("No User found with User Name : %s", userName));
        }
        return user.get();
    }

    public Page<User> findAll(Specification<User> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    public User findById(Long id) throws Exception {
        return repository
                .findById(id)
                .orElseThrow(() -> new Exception(String.format("User not found with id : %d", id)));
    }

    public User save(User user) {
        return repository.save(user);
    }

    public UserDTO createUser(UserRequest request) {
        User user = mapper.map(request);
        save(user);
        return mapper.map(user);
    }

    public Optional<User> getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null && auth.getPrincipal() != "anonymousUser") {
            return Optional.of((User) auth.getPrincipal());
        }
        return Optional.empty();
    }

    public User fetchUser() {
        return getLoggedInUser().orElseThrow(() -> new UserNotFoundException("User " + "Not found"));
    }
}
