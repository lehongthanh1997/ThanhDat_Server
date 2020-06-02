package com.bfwg.search;


import com.bfwg.model.Tour;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

public class OrderSpecification implements Specification<Tour> {

    private SearchCriteria criteria;

    public OrderSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate
            (Root<Tour> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        } else if (criteria.getOperation().equalsIgnoreCase("join")) {
//            Join<Order, Account> orderAccountJoin = root.join("account");
//
////            Join<Account, User> accountUserJoin = root.join("account").join("user");
//            Predicate predicate = builder.or(
//                    builder.like(root.get("id"), "%" + criteria.getValue() + "%"),
//                    builder.like(orderAccountJoin.get("email"), "%" + criteria.getValue() + "%"),
//                    builder.like(accountUserJoin.get("phoneNumber"), "%" + criteria.getValue() + "%"),
//                    builder.like(accountUserJoin.get("fullName"), "%" + criteria.getValue() + "%")
//            );
//            return predicate;
        }
        return null;
    }
}