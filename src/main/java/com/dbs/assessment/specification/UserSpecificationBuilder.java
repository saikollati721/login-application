package com.dbs.assessment.specification;

import com.dbs.assessment.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class UserSpecificationBuilder extends BaseSpecificationsBuilder {
    public UserSpecificationBuilder() {
        super(new ArrayList<>());
    }

    public Specification<User> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification result = new UserSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new UserSpecification(params.get(i)));
        }

        return result;
    }
}
