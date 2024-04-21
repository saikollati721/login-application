package com.dbs.assessment.specification;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author PratheepV
 */
@Data
@AllArgsConstructor
public class SearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;

    public Object getValue() {
        if (value instanceof String) {
            value = java.net.URLDecoder.decode(value.toString());
            return value.toString()
                    .replaceAll("tb_like", "~")
                    .replaceAll("tb_negation", "!")
                    .replaceAll("tb_single_quote", "'")
                    .replaceAll("tb_colon", ":")
                    .replaceAll("tb_semicolon", ";")
                    .replaceAll("tb_greater_than", ">")
                    .replaceAll("tb_lesser_than", "<")
                    .replaceAll("tb_at_sign", "@")
                    .replaceAll("tb_dollar_sign", "$")
                    .replaceAll("tb_asterisk", "*")
                    .replaceAll("tb_comma", ",");
        } else {
            return value;
        }
    }
}
