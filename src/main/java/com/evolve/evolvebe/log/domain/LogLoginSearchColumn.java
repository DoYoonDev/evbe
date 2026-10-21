package com.evolve.evolvebe.log.domain;

import org.springframework.data.jpa.domain.Specification;

import java.util.function.Function;

public enum LogLoginSearchColumn {
    MEMBER_ID("memberId", (keyword) ->
            (root, query, cb) -> cb.like(root.get("memberId"), "%" + keyword + "%")
    ),
    ACCESS_IP("accessIp", (keyword) ->
        (root, query, cb) -> cb.like(root.get("accessIp"), "%" + keyword + "%")
    );

    private final String value;
    private final Function<String, Specification<LogLogin>> specFunction;

    LogLoginSearchColumn(String value, Function<String, Specification<LogLogin>> specFunction) {
        this.value = value;
        this.specFunction = specFunction;
    }

    public static LogLoginSearchColumn from(String value) {
        for (LogLoginSearchColumn c : values()) {
            if (c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 검색 컬럼: " + value);
    }

    public Specification<LogLogin> toSpec(String keyword) {
        return specFunction.apply(keyword);
    }
}
