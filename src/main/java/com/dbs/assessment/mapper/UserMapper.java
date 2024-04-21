package com.dbs.assessment.mapper;

import com.dbs.assessment.dto.UserDTO;
import com.dbs.assessment.model.User;
import com.dbs.assessment.request.UserRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper {

    @Mapping(target = "userName", expression = "java(user.getUsername())")
    UserDTO map(User user);

    List<UserDTO> map(List<User> users);

    User map(UserRequest request);

    void merge(@MappingTarget User user, UserRequest request);
}
