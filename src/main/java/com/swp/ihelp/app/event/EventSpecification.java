package com.swp.ihelp.app.event;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.entity.SearchCriteria;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import lombok.SneakyThrows;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventSpecification implements Specification<EventEntity> {
    private SearchCriteria criteria;

    public EventSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @SneakyThrows
    @Override
    public Predicate toPredicate
            (Root<EventEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
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
                } else if (criteria.getKey().equals("eventCategories")) {
                    Join<EventEntity, EventCategoryEntity> join = root.join("eventCategories");
                    return builder.equal(join.<Integer>get("id"), criteria.getValue());
                } else if (criteria.getKey().equals("authorAccount")) {
                    Join<EventEntity, AccountEntity> join = root.join("authorAccount");
                    return builder.equal(join.<String>get("email"), criteria.getValue());
                }
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }


}
