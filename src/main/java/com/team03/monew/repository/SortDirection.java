package com.team03.monew.repository;

import com.querydsl.core.types.Order;

public enum SortDirection {

    ASC,
    DESC;

    public Order toQuerydslOrder() {
        if(this == ASC){
            return Order.ASC;
        }
        return Order.DESC;
    }
}
