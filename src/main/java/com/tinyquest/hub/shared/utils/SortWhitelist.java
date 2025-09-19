package com.tinyquest.hub.shared.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public final class SortWhitelist {
    private SortWhitelist() {}

    public static Pageable filter(Pageable pageable, List<String> allowed, Sort defaultSort) {
        var orders = pageable.getSort().stream()
                .filter(o -> allowed.contains(o.getProperty()))
                .map(o -> new Sort.Order(o.getDirection(), o.getProperty()).ignoreCase())
                .toList();

        Sort sort = orders.isEmpty() ? defaultSort : Sort.by(orders);
        int size = Math.min(pageable.getPageSize(), 100);
        return PageRequest.of(pageable.getPageNumber(), size, sort);
    }
}
