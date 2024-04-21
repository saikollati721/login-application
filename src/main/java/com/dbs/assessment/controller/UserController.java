package com.dbs.assessment.controller;

import com.dbs.assessment.dto.UserDTO;
import com.dbs.assessment.exception.UserNotFoundException;
import com.dbs.assessment.mapper.UserMapper;
import com.dbs.assessment.model.User;
import com.dbs.assessment.request.UserRequest;
import com.dbs.assessment.service.UserService;
import com.dbs.assessment.specification.UserSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserService service;
    private final UserMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<UserDTO> index(
            UserSpecificationBuilder builder, @PageableDefault(value = 25, page = 0) Pageable pageable) {
        log.info("Request GET /user/list");

        Specification<User> spec = builder.build();
        Page<User> users = service.findAll(spec, pageable);
        List<UserDTO> userDTOS = mapper.map(users.getContent());
        return new PageImpl<>(userDTOS, pageable, users.getTotalElements());
    }

    @PostMapping
    public UserDTO create(@RequestBody UserRequest request) {
        log.info("Request POST /user with request : " + request);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return service.createUser(request);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable(value = "id") Long id, @RequestBody UserRequest request) throws Exception {
        log.info(String.format("Request PUT /user/%d with request : " + request, id));
        User user = service.findById(id);
        mapper.merge(user, request);
        service.save(user);
        return mapper.map(user);
    }

}
