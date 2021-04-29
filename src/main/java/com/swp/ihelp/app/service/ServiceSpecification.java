package com.swp.ihelp.app.service;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.entity.SearchCriteria;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import com.swp.ihelp.app.status.StatusEntity;
import lombok.SneakyThrows;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceSpecification implements Specification<ServiceEntity> {
    private SearchCriteria criteria;

    public ServiceSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @SneakyThrows
    @Override
    public Predicate toPredicate
            (Root<ServiceEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        //Greater than
        if (criteria.getOperation().equalsIgnoreCase(">")) {
            if (criteria.getKey().contains("Date")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date parsedDate = dateFormat.parse(criteria.getValue().toString());
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                return builder.greaterThanOrEqualTo(
                        root.<Timestamp>get(criteria.getKey()), timestamp);
            }
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
            //Less than
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            if (criteria.getKey().contains("Date")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date parsedDate = dateFormat.parse(criteria.getValue().toString());
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                return builder.lessThanOrEqualTo(
                        root.<Timestamp>get(criteria.getKey()), timestamp);
            }
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getValue().toString());
            //Equal to
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                if (criteria.getKey().contains("Date")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsedDate = dateFormat.parse(criteria.getValue().toString());
                    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                    return builder.equal(
                            root.<Timestamp>get(criteria.getKey()), timestamp);
                } else if (criteria.getKey().equals("serviceCategories")) {
                    Join<ServiceEntity, EventCategoryEntity> join = root.join("serviceCategories");
                    return builder.equal(join.<Integer>get("id"), criteria.getValue());
                } else if (criteria.getKey().equals("authorAccount")) {
                    Join<ServiceEntity, AccountEntity> join = root.join("authorAccount");
                    return builder.equal(join.<String>get("email"), criteria.getValue());
                } else if (criteria.getKey().equals("status")) {
                    Join<ServiceEntity, StatusEntity> join = root.join("status");
                    return builder.equal(join.<Integer>get("id"), criteria.getValue());
                }
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        } else if (criteria.getOperation().equalsIgnoreCase("!")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.notLike(
                        root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                if (criteria.getKey().contains("Date")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsedDate = dateFormat.parse(criteria.getValue().toString());
                    Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                    return builder.notEqual(
                            root.<Timestamp>get(criteria.getKey()), timestamp);
                } else if (criteria.getKey().equals("serviceCategories")) {
                    Join<ServiceEntity, EventCategoryEntity> join = root.join("serviceCategories");
                    return builder.notEqual(join.<Integer>get("id"), criteria.getValue());
                } else if (criteria.getKey().equals("authorAccount")) {
                    Join<ServiceEntity, AccountEntity> join = root.join("authorAccount");
                    return builder.notEqual(join.<String>get("email"), criteria.getValue());
                } else if (criteria.getKey().equals("status")) {
                    Join<ServiceEntity, StatusEntity> join = root.join("status");
                    return builder.notEqual(join.<Integer>get("id"), criteria.getValue());
                }
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
