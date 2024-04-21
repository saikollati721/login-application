package com.dbs.assessment.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String phoneNumber;
}
