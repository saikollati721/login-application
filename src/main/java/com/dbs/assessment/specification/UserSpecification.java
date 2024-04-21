package com.dbs.assessment.specification;

import com.dbs.assessment.model.User;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.extern.log4j.Log4j2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Log4j2
public class UserSpecification extends BaseSpecification<User> {
    public UserSpecification(SearchCriteria criteria) {
        super(criteria);
    }

    @Override
    protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
        switch (key) {
            case "createdDate":
                return parseDate(value, op);
            case "updatedDate":
                return parseDate(value, op);
            default:
                return super.getEnumValueIfEnum(key, value, op);
        }
    }

    @Override
    protected Expression<String> getPath(SearchCriteria criteria, Root<User> root) {
        return root.get(criteria.getKey());
    }

    private Object parseDate(Object value, SearchOperation op) {
        String valueStr = value.toString();
        if (op.equals(SearchOperation.LESS_THAN)) valueStr = valueStr + " 23:59:59";
        if (op.equals(SearchOperation.GREATER_THAN)) valueStr = valueStr + " 00:00:00";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            return format.parse(valueStr);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
