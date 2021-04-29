package com.swp.ihelp.app.event;

import com.swp.ihelp.app.entity.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EventSpecificationBuilder {

    private final List<SearchCriteria> params;

    public EventSpecificationBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    public EventSpecificationBuilder with(String key, String operation, Object value, String orPredicate) {
        params.add(new SearchCriteria(orPredicate, key, operation, value));
        return this;
    }

    public Specification<EventEntity> build() {
        if (params.size() == 0) {
            return null;
        }
        Specification<EventEntity> result = new EventSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i - 1).isOrPredicate()
                    ? Specification.where(result).or(new EventSpecification(params.get(i)))
                    : Specification.where(result).and(new EventSpecification(params.get(i)));
        }
        return result;
    }

//    public Specification<EventEntity> build() {
//        if (params.size() == 0) {
//            return null;
//        }
//
//        List<Specification> specs = params.stream()
//                .map(EventSpecification::new)
//                .collect(Collectors.toList());
//
//        Specification result = specs.get(0);
//
//        for (int i = 1; i < params.size(); i++) {
//            logger.info("isOr:" + params.get(i).isOrPredicate());
//            result = params.get(i).isOrPredicate() ?
//                    Specification.where(result)
//                            .or(specs.get(i))
//                    : Specification.where(result).and(specs.get(i));
//        }
//        return result;
//    }
}
