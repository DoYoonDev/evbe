package com.evolve.evolvebe.member.domain;

import java.util.function.BiFunction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public enum SearchColumn {
    ID("id", (repo, param) -> repo.findByIdContaining(param.keyword(), param.pageable())),
    KO_NAME("koName", (repo, param) -> repo.findByKoNameContaining(param.keyword(), param.pageable())),
    EN_NAME("enName", (repo, param) -> repo.findByEnNameContaining(param.keyword(), param.pageable())),
    EMAIL("email", (repo, param) -> repo.findByEmailContaining(param.keyword(), param.pageable()));

    private final String value;
    private final BiFunction<MemberRepository, SearchParams, Page<Member>> searchFunction;

    SearchColumn(String value, BiFunction<MemberRepository, SearchParams, Page<Member>> searchFunction) {
        this.value = value;
        this.searchFunction = searchFunction;
    }

    public static SearchColumn from(String value) {
        for (SearchColumn c : values()) {
            if (c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 검색 컬럼: " + value);
    }

    public Page<Member> search(MemberRepository repo, String keyword, Pageable pageable) {
        return searchFunction.apply(repo, new SearchParams(keyword, pageable));
    }

    private record SearchParams(String keyword, Pageable pageable) {}
}
