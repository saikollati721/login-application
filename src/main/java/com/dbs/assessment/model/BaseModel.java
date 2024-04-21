package com.dbs.assessment.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public class BaseModel implements Serializable {

    @Column(name = "created_at")
    @CreationTimestamp
    public Timestamp createdDate;

    @Column(name = "updated_at")
    @UpdateTimestamp
    public Timestamp updatedDate;
}
