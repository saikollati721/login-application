package com.dbs.assessment.mapper;

import com.dbs.assessment.model.LoginTracker;
import com.dbs.assessment.request.LoginRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface LoginTrackerMapper {

    LoginTracker map(LoginRequest loginRequest);
}
