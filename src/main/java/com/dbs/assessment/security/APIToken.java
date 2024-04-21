package com.dbs.assessment.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class APIToken {

    private final Long userId;
    private final String userName;
}
