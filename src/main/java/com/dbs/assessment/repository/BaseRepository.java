package com.dbs.assessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface BaseRepository<T, ID>
        extends JpaSpecificationExecutor<T>, JpaRepository<T, ID>, PagingAndSortingRepository<T, ID> {}
