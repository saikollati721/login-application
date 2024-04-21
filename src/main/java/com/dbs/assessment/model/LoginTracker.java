package com.dbs.assessment.model;

import com.dbs.assessment.enums.LoginStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "login_tracker")
@Data
@ToString
public class LoginTracker extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LoginStatus status;

}
