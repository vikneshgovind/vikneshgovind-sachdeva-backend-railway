package com.sachdeva.roadlines.specification;

import com.sachdeva.roadlines.Entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<UserEntity> filterUsers(
            String email,
            String userId,
            Boolean isAccountVerified,
            String role
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (email != null && !email.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            if (userId != null && !userId.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("userId")), "%" + userId.toLowerCase() + "%"));
            }

            if (role != null && !role.isEmpty()) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            if (isAccountVerified != null) {
                predicates.add(cb.equal(root.get("isAccountVerified"), isAccountVerified));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
